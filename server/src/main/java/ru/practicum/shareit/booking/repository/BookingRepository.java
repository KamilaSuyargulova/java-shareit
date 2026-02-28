package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.booker " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH i.owner " +
            "WHERE b.id = :bookingId")
    Optional<Booking> findByIdWithDetails(@Param("bookingId") Long bookingId);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.booker " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH i.owner " +
            "WHERE b.booker.id = :bookerId " +
            "ORDER BY b.startBooking DESC")
    List<Booking> findAllByBookerId(@Param("bookerId") Long bookerId);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.booker " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH i.owner " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.startBooking < :now " +
            "AND b.endBooking > :now " +
            "ORDER BY b.startBooking DESC")
    List<Booking> findCurrentByBookerId(@Param("bookerId") Long bookerId,
                                        @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.booker " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH i.owner " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.endBooking < :now " +
            "ORDER BY b.startBooking DESC")
    List<Booking> findPastByBookerId(@Param("bookerId") Long bookerId,
                                     @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.booker " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH i.owner " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.startBooking > :now " +
            "ORDER BY b.startBooking DESC")
    List<Booking> findFutureByBookerId(@Param("bookerId") Long bookerId,
                                       @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.booker " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH i.owner " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.status = :status " +
            "ORDER BY b.startBooking DESC")
    List<Booking> findByBookerIdAndStatus(@Param("bookerId") Long bookerId,
                                          @Param("status") BookingStatus status);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.booker " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH i.owner " +
            "WHERE i.owner.id = :ownerId " +
            "ORDER BY b.startBooking DESC")
    List<Booking> findAllByOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.booker " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH i.owner " +
            "WHERE i.owner.id = :ownerId " +
            "AND b.startBooking < :now " +
            "AND b.endBooking > :now " +
            "ORDER BY b.startBooking DESC")
    List<Booking> findCurrentByOwnerId(@Param("ownerId") Long ownerId,
                                       @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.booker " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH i.owner " +
            "WHERE i.owner.id = :ownerId " +
            "AND b.endBooking < :now " +
            "ORDER BY b.startBooking DESC")
    List<Booking> findPastByOwnerId(@Param("ownerId") Long ownerId,
                                    @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.booker " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH i.owner " +
            "WHERE i.owner.id = :ownerId " +
            "AND b.startBooking > :now " +
            "ORDER BY b.startBooking DESC")
    List<Booking> findFutureByOwnerId(@Param("ownerId") Long ownerId,
                                      @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.booker " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH i.owner " +
            "WHERE i.owner.id = :ownerId " +
            "AND b.status = :status " +
            "ORDER BY b.startBooking DESC")
    List<Booking> findByOwnerIdAndStatus(@Param("ownerId") Long ownerId,
                                         @Param("status") BookingStatus status);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id IN :itemIds " +
            "AND b.startBooking < :now " +
            "AND b.startBooking IN (SELECT MAX(b2.startBooking) FROM Booking b2 " +
            "WHERE b2.item.id = b.item.id AND b2.startBooking < :now GROUP BY b2.item.id)")
    List<Booking> findLastBookingsForItems(@Param("itemIds") List<Long> itemIds,
                                           @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id IN :itemIds " +
            "AND b.startBooking > :now " +
            "AND b.startBooking IN (SELECT MIN(b2.startBooking) FROM Booking b2 " +
            "WHERE b2.item.id = b.item.id AND b2.startBooking > :now GROUP BY b2.item.id)")
    List<Booking> findNextBookingsForItems(@Param("itemIds") List<Long> itemIds,
                                           @Param("now") LocalDateTime now);

    boolean existsByBookerIdAndItemIdAndStatusAndEndBookingBefore(
            Long bookerId,
            Long itemId,
            BookingStatus status,
            LocalDateTime time
    );

}