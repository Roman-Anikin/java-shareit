package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.util.OffsetPageRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository repository;
    private final ItemService itemService;
    private final UserService userService;
    private final BookingMapper bookingMapper;
    private final UserMapper userMapper;

    public BookingServiceImpl(BookingRepository repository,
                              ItemService itemService,
                              UserService userService,
                              BookingMapper bookingMapper,
                              UserMapper userMapper) {
        this.repository = repository;
        this.itemService = itemService;
        this.userService = userService;
        this.bookingMapper = bookingMapper;
        this.userMapper = userMapper;
    }

    @Override
    public BookingDto add(Long userId, BookingDto bookingDto) {
        Booking booking = bookingMapper.convertFromDto(bookingDto);
        checkDate(booking);
        booking.setItem(itemService.getItemById(bookingDto.getItemId()));
        if (booking.getItem().getOwner().getId().equals(userId)) {
            throw new ObjectNotFoundException("Владелец не может арендовать собственную вещь");
        }
        booking.setBooker(userMapper.convertFromDto(userService.getById(userId)));
        if (booking.getItem().getAvailable()) {
            log.info("Добавлено бронирование {}", booking);
            return bookingMapper.convertToDto(repository.save(booking));
        }
        throw new ValidationException("Предмет " + booking.getItem() + " не доступен");
    }

    @Override
    public BookingDto makeApprove(Long ownerId, Long bookingId, boolean approved) {
        Booking booking = checkBooking(bookingId);
        if (booking.getItem().getOwner().getId().equals(ownerId)) {
            if (booking.getStatus().equals(BookingStatus.WAITING)) {
                if (approved) {
                    booking.setStatus(BookingStatus.APPROVED);
                } else {
                    booking.setStatus(BookingStatus.REJECTED);
                }
                log.info("Бронированию {} установлен новый статус {}", booking, booking.getStatus());
                return bookingMapper.convertToDto(repository.save(booking));
            }
            throw new ValidationException("Изменить статус бронирования невозможно");
        }
        throw new ObjectNotFoundException("Изменить статус бронирования может только владелец предмета");
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
        checkUser(bookerId);
        checkState(state);
        List<BookingDto> bookings = new ArrayList<>();
        switch (BookingState.valueOf(state)) {
            case ALL:
                bookings = bookingMapper.convertToDto(repository.findByBookerId(bookerId, getPagination(from, size)));
                break;
            case CURRENT:
                bookings = bookingMapper.convertToDto(repository.findByBookerIdAndStartBeforeAndEndAfter(
                        bookerId, LocalDateTime.now(), LocalDateTime.now(), getPagination(from, size)));
                break;
            case PAST:
                bookings = bookingMapper.convertToDto(repository.findByBookerIdAndEndBefore(
                        bookerId, LocalDateTime.now(), getPagination(from, size)));
                break;
            case FUTURE:
                bookings = bookingMapper.convertToDto(repository.findByBookerIdAndStartAfter(
                        bookerId, LocalDateTime.now(), getPagination(from, size)));
                break;
            case WAITING:
                bookings = bookingMapper.convertToDto(repository.findByBookerIdAndStatus(
                        bookerId, BookingStatus.WAITING, getPagination(from, size)));
                break;
            case REJECTED:
                bookings = bookingMapper.convertToDto(repository.findByBookerIdAndStatus(
                        bookerId, BookingStatus.REJECTED, getPagination(from, size)));
        }
        log.info("Получен список бронирований {} для пользователя {}", bookings, userService.getById(bookerId));
        return bookings;
    }

    @Override
    public List<BookingDto> getByOwnerAndState(Long ownerId, String state, Integer from, Integer size) {
        checkUser(ownerId);
        checkState(state);
        List<BookingDto> bookings = new ArrayList<>();
        if (itemService.getByOwner(ownerId, 0, null).size() > 0) {
            switch (BookingState.valueOf(state)) {
                case ALL:
                    bookings = bookingMapper.convertToDto(repository.findByItemOwnerId(ownerId,
                            getPagination(from, size)));
                    break;
                case CURRENT:
                    bookings = bookingMapper.convertToDto(repository.findByItemOwnerIdAndStartBeforeAndEndAfter(
                            ownerId, LocalDateTime.now(), LocalDateTime.now(), getPagination(from, size)));
                    break;
                case PAST:
                    bookings = bookingMapper.convertToDto(repository.findByItemOwnerIdAndEndBefore(
                            ownerId, LocalDateTime.now(), getPagination(from, size)));
                    break;
                case FUTURE:
                    bookings = bookingMapper.convertToDto(repository.findByItemOwnerIdAndStartAfter(
                            ownerId, LocalDateTime.now(), getPagination(from, size)));
                    break;
                case WAITING:
                    bookings = bookingMapper.convertToDto(repository.findByItemOwnerIdAndStatus(
                            ownerId, BookingStatus.WAITING, getPagination(from, size)));
                    break;
                case REJECTED:
                    bookings = bookingMapper.convertToDto(repository.findByItemOwnerIdAndStatus(
                            ownerId, BookingStatus.REJECTED, getPagination(from, size)));
            }
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
        return repository.findByItemIdAndBookerIdAndEndBefore(itemId, userId, time);
    }

    private void checkDate(Booking booking) {
        if (booking.getEnd().isAfter(booking.getStart())) {
            return;
        }
        throw new ValidationException("Дата конца аренды не может быть раньше даты начала аренды");
    }

    private void checkUser(Long userId) {
        if (userService.getById(userId) == null) {
            throw new ObjectNotFoundException("Пользователь с id " + userId + " не найден");
        }
    }

    private Booking checkBooking(Long bookingId) {
        Optional<Booking> booking = repository.findById(bookingId);
        if (booking.isEmpty()) {
            throw new ObjectNotFoundException("Бронирование с id " + bookingId + " не найдено");
        }
        return booking.get();
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