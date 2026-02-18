package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerId(Long bookerId, Sort sort);

    List<Booking> findByItemOwnerId(Long ownerId, Sort sort);

    List<Booking> findByBookerIdAndStatus(Long bookerId, BookingStatus status, Sort sort);

    List<Booking> findByItemOwnerIdAndStatus(Long ownerId, BookingStatus status, Sort sort);

    List<Booking> findByBookerIdAndStartLessThanEqualAndEndGreaterThan(Long bookerId, LocalDateTime now1,
                                                                       LocalDateTime now2, Sort sort);

    List<Booking> findByItemOwnerIdAndStartLessThanEqualAndEndGreaterThan(Long ownerId, LocalDateTime now1,
                                                                          LocalDateTime now2, Sort sort);

    List<Booking> findByBookerIdAndEndBefore(Long bookerId, LocalDateTime end, Sort sort);

    List<Booking> findByItemOwnerIdAndEndBefore(Long ownerId, LocalDateTime end, Sort sort);

    List<Booking> findByBookerIdAndStartAfter(Long bookerId, LocalDateTime start, Sort sort);

    List<Booking> findByItemOwnerIdAndStartAfter(Long ownerId, LocalDateTime start, Sort sort);

    @Query("select case when count(b) > 0 then true else false end from Booking b " +
            "where b.booker.id = ?1 and b.item.id = ?2 and b.status = 'APPROVED' and b.end < ?3")
    boolean existsByBookerIdAndItemIdAndStatusApprovedAndEndBefore(Long bookerId, Long itemId, LocalDateTime now);

    @Query("select b from Booking b where b.item.id = ?1 and b.status = 'APPROVED' and b.start < ?2 order by b.end desc")
    List<Booking> findLastBookingByItemId(Long itemId, LocalDateTime now, Sort sort);

    @Query("select b from Booking b where b.item.id = ?1 and b.status = 'APPROVED' and b.start > ?2 order by b.start asc")
    List<Booking> findNextBookingByItemId(Long itemId, LocalDateTime now, Sort sort);
}