package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    Optional<ItemRequest> findById(Long id);

    List<ItemRequest> findByRequestor_IdOrderByCreatedDesc(Long requestorId);

    List<ItemRequest> findAllByRequestorIdNot(Long id);
}