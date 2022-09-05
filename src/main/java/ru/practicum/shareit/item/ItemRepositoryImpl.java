package ru.practicum.shareit.item;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ItemRepositoryImpl {

    private final HashMap<Long, Item> items = new HashMap<>();

    public Item add(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    public Item update(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    public Item getById(Long itemId) {
        return items.get(itemId);
    }

    public List<Item> getByOwner(Long ownerId) {
        return items.values()
                .stream()
                .filter(item -> Objects.equals(item.getOwner().getId(), ownerId))
                .collect(Collectors.toList());
    }

    public List<Item> searchByText(String text) {
        return items.values()
                .stream()
                .filter(item ->
                        item.getAvailable() &&
                                (item.getName().toLowerCase().contains(text) ||
                                        item.getDescription().toLowerCase().contains(text)))
                .collect(Collectors.toList());
    }
}
