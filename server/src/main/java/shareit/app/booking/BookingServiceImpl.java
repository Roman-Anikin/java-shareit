package shareit.app.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shareit.app.booking.dto.BookingDto;
import shareit.app.exception.ObjectNotFoundException;
import shareit.app.exception.ValidationException;
import shareit.app.item.ItemService;
import shareit.app.user.UserMapper;
import shareit.app.user.UserService;
import shareit.app.util.OffsetPageRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository repository;
    private final ItemService itemService;
    private final UserService userService;
    private final BookingMapper bookingMapper;
    private final UserMapper userMapper;

    @Override
    public BookingDto add(Long userId, BookingDto bookingDto) {
        Booking booking = bookingMapper.convertFromDto(bookingDto);
        checkDate(booking);
        booking.setItem(itemService.getItemById(bookingDto.getItemId()));
        if (booking.getItem().getOwner().getId().equals(userId)) {
            throw new ObjectNotFoundException("Владелец не может арендовать собственную вещь");
        }
        booking.setBooker(userMapper.convertFromDto(userService.getById(userId)));
        if (!booking.getItem().getAvailable()) {
            throw new ValidationException("Предмет " + booking.getItem() + " не доступен");
        }
        repository.save(booking);
        log.info("Добавлено бронирование {}", booking);
        return bookingMapper.convertToDto(booking);
    }

    @Override
    @Transactional
    public BookingDto makeApprove(Long ownerId, Long bookingId, boolean approved) {
        Booking booking = checkBooking(bookingId);
        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new ObjectNotFoundException("Изменить статус бронирования может только владелец предмета");
        }
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new ValidationException("Изменить статус бронирования невозможно");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        log.info("Бронированию {} установлен новый статус {}", booking, booking.getStatus());
        return bookingMapper.convertToDto(booking);
    }

    @Override
    public BookingDto getById(Long userId, Long bookingId) {
        Booking booking = checkBooking(bookingId);
        if (booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId)) {
            log.info("Получено бронирование {}", booking);
            return bookingMapper.convertToDto(booking);
        }
        throw new ObjectNotFoundException("Просмотреть бронирование может либо владелец вещи, либо автор аренды");
    }

    @Override
    public List<BookingDto> getByUserAndState(Long bookerId, String state, Integer from, Integer size) {
        userService.getById(bookerId);
        checkState(state);
        List<BookingDto> bookings = switch (BookingState.valueOf(state)) {
            case ALL -> bookingMapper.convertToDto(repository.findByBookerId(bookerId, getPagination(from, size)));
            case CURRENT -> bookingMapper.convertToDto(repository.findByBookerIdAndStartBeforeAndEndAfter(
                    bookerId, LocalDateTime.now(), LocalDateTime.now(), getPagination(from, size)));
            case PAST -> bookingMapper.convertToDto(repository.findByBookerIdAndEndBefore(
                    bookerId, LocalDateTime.now(), getPagination(from, size)));
            case FUTURE -> bookingMapper.convertToDto(repository.findByBookerIdAndStartAfter(
                    bookerId, LocalDateTime.now(), getPagination(from, size)));
            case WAITING -> bookingMapper.convertToDto(repository.findByBookerIdAndStatus(
                    bookerId, BookingStatus.WAITING, getPagination(from, size)));
            case REJECTED -> bookingMapper.convertToDto(repository.findByBookerIdAndStatus(
                    bookerId, BookingStatus.REJECTED, getPagination(from, size)));
        };
        log.info("Получен список бронирований {} для пользователя {}", bookings, userService.getById(bookerId));
        return bookings;
    }

    @Override
    public List<BookingDto> getByOwnerAndState(Long ownerId, String state, Integer from, Integer size) {
        userService.getById(ownerId);
        checkState(state);
        List<BookingDto> bookings = new ArrayList<>();
        if (!itemService.getByOwner(ownerId, 0, size).isEmpty()) {
            bookings = switch (BookingState.valueOf(state)) {
                case ALL -> bookingMapper.convertToDto(repository.findByItemOwnerId(ownerId,
                        getPagination(from, size)));
                case CURRENT -> bookingMapper.convertToDto(repository.findByItemOwnerIdAndStartBeforeAndEndAfter(
                        ownerId, LocalDateTime.now(), LocalDateTime.now(), getPagination(from, size)));
                case PAST -> bookingMapper.convertToDto(repository.findByItemOwnerIdAndEndBefore(
                        ownerId, LocalDateTime.now(), getPagination(from, size)));
                case FUTURE -> bookingMapper.convertToDto(repository.findByItemOwnerIdAndStartAfter(
                        ownerId, LocalDateTime.now(), getPagination(from, size)));
                case WAITING -> bookingMapper.convertToDto(repository.findByItemOwnerIdAndStatus(
                        ownerId, BookingStatus.WAITING, getPagination(from, size)));
                case REJECTED -> bookingMapper.convertToDto(repository.findByItemOwnerIdAndStatus(
                        ownerId, BookingStatus.REJECTED, getPagination(from, size)));
            };
        }
        log.info("Получен список бронирований {} для владельца {}", bookings, userService.getById(ownerId));
        return bookings;
    }

    @Override
    public BookingDto getLastBooking(Long itemId) {
        Booking booking = repository.findFirstByItemIdAndEndBefore(itemId, LocalDateTime.now(), getSorting());
        return booking == null ? null : bookingMapper.convertToDto(booking);
    }

    @Override
    public BookingDto getNextBooking(Long itemId) {
        Booking booking = repository.findFirstByItemIdAndStartAfter(itemId, LocalDateTime.now(),
                Sort.by(Sort.Direction.ASC, "start"));
        return booking == null ? null : bookingMapper.convertToDto(booking);
    }

    @Override
    public Booking getByItemId(Long itemId, Long userId, LocalDateTime time) {
        return repository.findByItemIdAndBookerIdAndEndBefore(itemId, userId, time)
                .orElseThrow(() ->
                        new ValidationException("Бронирование не найдено"));
    }

    private void checkDate(Booking booking) {
        if (booking.getEnd().isBefore(booking.getStart())) {
            throw new ValidationException("Дата конца аренды не может быть раньше даты начала аренды");
        }
    }

    private Booking checkBooking(Long bookingId) {
        return repository.findById(bookingId)
                .orElseThrow(() ->
                        new ObjectNotFoundException("Бронирование с id " + bookingId + " не найдено"));
    }

    private void checkState(String string) {
        for (BookingState state : BookingState.values()) {
            if (state.toString().equals(string)) {
                return;
            }
        }
        throw new ValidationException("Unknown state: " + string);
    }

    private Pageable getPagination(Integer from, Integer size) {
        return new OffsetPageRequest(from, size, getSorting());
    }

    private Sort getSorting() {
        return Sort.by(Sort.Direction.DESC, "end");
    }
}