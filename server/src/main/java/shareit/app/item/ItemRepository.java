package shareit.app.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> getAllByOwnerId(Long ownerId, Pageable pageable);

    Optional<Item> getByIdAndOwnerId(Long itemId, Long ownerId);

    @Query(value = "SELECT * " +
            "FROM items " +
            "WHERE (POSITION(?1 IN LOWER(name)) > 0 OR POSITION(?1 IN LOWER(description)) > 0)" +
            "AND is_available", nativeQuery = true)
    List<Item> getAllByText(String text, Pageable pageable);

    List<Item> getAllByRequestId(Long requestId);
}
