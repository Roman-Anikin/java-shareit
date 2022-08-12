package ru.practicum.shareit.item;

import java.util.List;

public interface ItemRepository {

    Item add(Item item);

    Item update(Item item);

    Item getById(Long itemId);

    List<Item> getByOwner(Long ownerId);

    List<Item> searchByText(String text);
}
