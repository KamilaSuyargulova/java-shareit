package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("SELECT DISTINCT i FROM Item i " +
            "JOIN FETCH i.owner " +
            "LEFT JOIN FETCH i.request " +
            "LEFT JOIN FETCH i.comments c " +
            "LEFT JOIN FETCH c.author " +
            "WHERE i.owner.id = :ownerId")
    List<Item> findByOwnerIdWithDetails(@Param("ownerId") Long ownerId);

    @Query("SELECT DISTINCT i FROM Item i " +
            "JOIN FETCH i.owner " +
            "LEFT JOIN FETCH i.request " +
            "WHERE i.available = true " +
            "AND (LOWER(i.name) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(i.description) LIKE LOWER(CONCAT('%', :text, '%')))")
    List<Item> searchAvailableItemsByText(@Param("text") String text);

    @Query("SELECT DISTINCT i FROM Item i " +
            "JOIN FETCH i.owner " +
            "LEFT JOIN FETCH i.request " +
            "WHERE i.id = :id")
    Optional<Item> findByIdWithDetails(@Param("id") Long itemId);

    @Query("SELECT i FROM Item i " +
            "JOIN FETCH i.owner " +
            "LEFT JOIN FETCH i.request " +
            "LEFT JOIN FETCH i.request.requestor " +
            "WHERE i.request.requestor.id = :requestorId")
    List<Item> findByRequestorIdWithDetails(@Param("requestorId") Long requestorId);

    List<Item> findByRequest_Id(Long requestId);

}