package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceTest {
    private final BookingService bookingService;
    private final EntityManager em;

    private User owner;
    private User booker;
    private Item item;
    private Long waitingBookingId;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .name("John Owner")
                .email("owner@example.com")
                .build();
        em.persist(owner);

        booker = User.builder()
                .name("Jane Booker")
                .email("booker@example.com")
                .build();
        em.persist(booker);

        item = Item.builder()
                .name("Laptop")
                .description("Gaming laptop")
                .available(true)
                .owner(owner)
                .build();
        em.persist(item);

        var waitingBooking = ru.practicum.shareit.booking.model.Booking.builder()
                .startBooking(LocalDateTime.now().plusDays(1))
                .endBooking(LocalDateTime.now().plusDays(3))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
        em.persist(waitingBooking);
        em.flush();

        waitingBookingId = waitingBooking.getId();
    }

    @Test
    void approveBooking_ShouldApproveBooking_WhenUserIsOwnerAndApprovedIsTrue() {
        BookingResponseDto result = bookingService.approveBooking(owner.getId(), waitingBookingId, true);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(waitingBookingId);
        assertThat(result.getStatus()).isEqualTo(BookingStatus.APPROVED);

        TypedQuery<ru.practicum.shareit.booking.model.Booking> query = em.createQuery(
                "SELECT b FROM Booking b WHERE b.id = :id",
                ru.practicum.shareit.booking.model.Booking.class);
        query.setParameter("id", waitingBookingId);
        var updatedBooking = query.getSingleResult();

        assertThat(updatedBooking.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void approveBooking_ShouldRejectBooking_WhenUserIsOwnerAndApprovedIsFalse() {
        BookingResponseDto result = bookingService.approveBooking(owner.getId(), waitingBookingId, false);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(waitingBookingId);
        assertThat(result.getStatus()).isEqualTo(BookingStatus.REJECTED);

        TypedQuery<ru.practicum.shareit.booking.model.Booking> query = em.createQuery(
                "SELECT b FROM Booking b WHERE b.id = :id",
                ru.practicum.shareit.booking.model.Booking.class);
        query.setParameter("id", waitingBookingId);
        var updatedBooking = query.getSingleResult();

        assertThat(updatedBooking.getStatus()).isEqualTo(BookingStatus.REJECTED);
    }

    @Test
    void approveBooking_ShouldThrowNotFoundException_WhenBookingDoesNotExist() {
        Long nonExistentBookingId = 999L;

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.approveBooking(owner.getId(), nonExistentBookingId, true));

        assertThat(exception.getMessage()).contains("Booking with id=" + nonExistentBookingId + " not found");
    }

    @Test
    void approveBooking_ShouldThrowBadRequestException_WhenUserIsNotOwner() {
        User notOwner = User.builder()
                .name("Not Owner")
                .email("notowner@example.com")
                .build();
        em.persist(notOwner);
        em.flush();

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> bookingService.approveBooking(notOwner.getId(), waitingBookingId, true));

        assertThat(exception.getMessage()).contains("User with id=" + notOwner.getId() + " is not owner of booking with id=" + waitingBookingId);

        TypedQuery<ru.practicum.shareit.booking.model.Booking> query = em.createQuery(
                "SELECT b FROM Booking b WHERE b.id = :id",
                ru.practicum.shareit.booking.model.Booking.class);
        query.setParameter("id", waitingBookingId);
        var unchangedBooking = query.getSingleResult();

        assertThat(unchangedBooking.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void approveBooking_ShouldThrowBadRequestException_WhenBookingStatusIsNotWaiting() {
        var approvedBooking = ru.practicum.shareit.booking.model.Booking.builder()
                .startBooking(LocalDateTime.now().plusDays(2))
                .endBooking(LocalDateTime.now().plusDays(4))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        em.persist(approvedBooking);
        em.flush();

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> bookingService.approveBooking(owner.getId(), approvedBooking.getId(), true));

        assertThat(exception.getMessage()).isEqualTo("Booking status has already been changed");
    }

    @Test
    void approveBooking_ShouldThrowBadRequestException_WhenOwnerTriesToApproveTwice() {
        bookingService.approveBooking(owner.getId(), waitingBookingId, true);

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> bookingService.approveBooking(owner.getId(), waitingBookingId, true));

        assertThat(exception.getMessage()).isEqualTo("Booking status has already been changed");
    }
}