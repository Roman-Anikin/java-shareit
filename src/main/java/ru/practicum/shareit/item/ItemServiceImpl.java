package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository repository;
    private final UserService userService;
    private Long id = 1L;

    public ItemServiceImpl(ItemRepository repository, UserService userService) {
        this.repository = repository;
        this.userService = userService;
    }

    @Override
    public Item add(Item item) {
        if (checkOwner(item.getOwner().getId())) {
            item.setId(id++);
            item.setOwner(userService.getById(item.getOwner().getId()));
            log.info("Добавлен предмет {}", item);
            return repository.add(item);
        }
        throw new ObjectNotFoundException("Пользователь с id " + item.getOwner().getId() + " не найден");
    }

    @Override
    public Item update(Long itemId, Long ownerId, Item item) {
        if (checkOwner(ownerId)) {
            if (getByOwner(ownerId)
                    .stream()
                    .anyMatch(item1 -> item1.getId().equals(itemId))) {
                Item newItem = getById(itemId);
                if (item.getName() != null) {
                    newItem.setName(item.getName());
                }
                if (item.getDescription() != null) {
                    newItem.setDescription(item.getDescription());
                }
                if (item.getAvailable() != null) {
                    newItem.setAvailable(item.getAvailable());
                }
                log.info("Обновлен предмет {}", newItem);
                return repository.update(newItem);
            }
            throw new ObjectNotFoundException("Предмет с id " + itemId + " не найден");
        }
        throw new ObjectNotFoundException("Владелец с id " + ownerId + " не найден");
    }

    @Override
    public Item getById(Long itemId) {
        Item item = repository.getById(itemId);
        log.info("Получен предмет {}", item);
        return item;
    }

    @Override
    public List<Item> getByOwner(Long ownerId) {
        List<Item> items = repository.getByOwner(ownerId);
        log.info("Получен список предметов {} пользователя {}", items, userService.getById(ownerId));
        return items;
    }

    @Override
    public List<Item> searchByText(String text) {
        text = text.toLowerCase().trim();
        List<Item> items = new ArrayList<>();
        if (!text.isEmpty()) {
            items = repository.searchByText(text);
        }
        log.info("Получен список предметов {} по поиску {}", items, text);
        return items;
    }

    private boolean checkOwner(Long id) {
        return userService.getAll()
                .stream()
                .anyMatch(user -> user.getId().equals(id));
    }
}
