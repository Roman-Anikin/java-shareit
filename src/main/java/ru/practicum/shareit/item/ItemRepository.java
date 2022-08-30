package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ItemRepository extends CrudRepository<Item, Long> {

    List<Item> getByOwnerIdOrderByIdAsc(Long ownerId);

    @Query(value = "SELECT * " +
            "FROM items AS it " +
            "WHERE (POSITION(?1 IN LOWER(it.name)) > 0 OR POSITION(?1 IN LOWER(it.description)) > 0)" +
            "AND it.is_available", nativeQuery = true)
    List<Item> searchByText(String text);
}
