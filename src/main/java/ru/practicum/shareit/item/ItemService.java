package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {

    Item add(Item item);

    Item update(Long itemId, Long ownerId, Item item);

    Item getById(Long itemId);

    List<Item> getByOwner(Long ownerId);

    List<Item> searchByText(String text);

}
