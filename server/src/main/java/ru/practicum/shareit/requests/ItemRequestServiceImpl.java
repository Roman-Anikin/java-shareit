package ru.practicum.shareit.requests;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.util.OffsetPageRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository repository;
    private final UserService userService;
    private final ItemService itemService;
    private final ItemRequestMapper requestMapper;

    @Override
    public ItemRequestDto add(Long userId, ItemRequestDto requestDto) {
        userService.getById(userId);
        ItemRequest request = requestMapper.convertFromDto(requestDto);
        request.getRequester().setId(userId);
        request.setCreated(LocalDateTime.now());
        repository.save(request);
        log.info("Добавлен запрос вещи {}", request);
        return requestMapper.convertToDto(request);
    }

    @Override
    public List<ItemRequestDto> getAllByRequester(Long requesterId) {
        userService.getById(requesterId);
        return setItems(repository.findAllByRequesterId(requesterId, getSorting()));
    }

    @Override
    public List<ItemRequestDto> getAllExceptRequester(Long requesterId, Integer from, Integer size) {
        userService.getById(requesterId);
        return setItems(repository.findAllByRequesterIdNot(requesterId, getPagination(from, size)));
    }

    @Override
    public ItemRequestDto getById(Long userId, Long requestId) {
        userService.getById(userId);
        ItemRequestDto request = requestMapper.convertToDto(checkRequest(requestId));
        request.setItems(itemService.getAllByRequestId(requestId));
        log.info("Получен запрос {}", request);
        return request;
    }

    public List<ItemRequestDto> setItems(List<ItemRequest> requests) {
        List<ItemRequestDto> requestDtos = new ArrayList<>();
        for (int i = 0; i < requests.size(); i++) {
            requestDtos.add(requestMapper.convertToDto(requests.get(i)));
            requestDtos.get(i).setItems(itemService.getAllByRequestId(requests.get(i).getId()));
        }
        log.info("Получен список запросов {}", requestDtos);
        return requestDtos;
    }

    private ItemRequest checkRequest(Long requestId) {
        return repository.findById(requestId)
                .orElseThrow(() ->
                        new ObjectNotFoundException("Запрос с id " + requestId + " не найден"));
    }

    private Pageable getPagination(Integer from, Integer size) {
        return new OffsetPageRequest(from, size, getSorting());
    }

    private Sort getSorting() {
        return Sort.by(Sort.Direction.DESC, "created");
    }
}