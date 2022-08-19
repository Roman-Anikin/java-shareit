package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private final HashMap<Long, Item> items = new HashMap<>();

    @Override
    public Item add(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item getById(Long itemId) {
        return items.get(itemId);
    }

    @Override
    public List<Item> getByOwner(Long ownerId) {
        return items.values()
                .stream()
                .filter(item -> Objects.equals(item.getOwner().getId(), ownerId))
                .collect(Collectors.toList());
    }

    @Override
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
