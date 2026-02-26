package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.dto.BookingResponseDto;
import ru.practicum.shareit.model.Booking;
import ru.practicum.shareit.model.BookingStatus;
import ru.practicum.shareit.repository.BookingRepository;
import ru.practicum.shareit.service.BookingService;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = ShareItApp.class)
@ActiveProfiles("test")
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceTest {
    private final BookingService bookingService;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private User owner;
    private User booker;
    private Item item;
    private Booking waitingBooking;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .name("John Owner")
                .email("owner@example.com")
                .build();
        owner = userRepository.save(owner);

        booker = User.builder()
                .name("Jane Booker")
                .email("booker@example.com")
                .build();
        booker = userRepository.save(booker);

        item = Item.builder()
                .name("Laptop")
                .description("Gaming laptop")
                .available(true)
                .owner(owner)
                .build();
        item = itemRepository.save(item);

        waitingBooking = Booking.builder()
                .startBooking(LocalDateTime.now().plusDays(1))
                .endBooking(LocalDateTime.now().plusDays(3))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
        waitingBooking = bookingRepository.save(waitingBooking);
    }

    @Test
    void approveBooking_ShouldApproveBooking_WhenUserIsOwnerAndApprovedIsTrue() {
        BookingResponseDto result = bookingService.approveBooking(owner.getId(), waitingBooking.getId(), true);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(waitingBooking.getId());
        assertThat(result.getStatus()).isEqualTo(BookingStatus.APPROVED);
        assertThat(result.getStart()).isEqualTo(waitingBooking.getStartBooking());
        assertThat(result.getEnd()).isEqualTo(waitingBooking.getEndBooking());

        assertThat(result.getBooker().getId()).isEqualTo(booker.getId());
        assertThat(result.getItem().getId()).isEqualTo(item.getId());

        Booking updatedBooking = bookingRepository.findById(waitingBooking.getId()).orElseThrow();
        assertThat(updatedBooking.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void approveBooking_ShouldRejectBooking_WhenUserIsOwnerAndApprovedIsFalse() {
        BookingResponseDto result = bookingService.approveBooking(owner.getId(), waitingBooking.getId(), false);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(waitingBooking.getId());
        assertThat(result.getStatus()).isEqualTo(BookingStatus.REJECTED);

        Booking updatedBooking = bookingRepository.findById(waitingBooking.getId()).orElseThrow();
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
        User savedNotOwner = userRepository.save(notOwner);

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> bookingService.approveBooking(savedNotOwner.getId(), waitingBooking.getId(), true));

        assertThat(exception.getMessage()).contains("User with id=" + savedNotOwner.getId() + " is not owner of booking with id=" + waitingBooking.getId());

        Booking unchangedBooking = bookingRepository.findById(waitingBooking.getId()).orElseThrow();
        assertThat(unchangedBooking.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void approveBooking_ShouldThrowBadRequestException_WhenBookingStatusIsNotWaiting() {
        Booking approvedBooking = Booking.builder()
                .startBooking(LocalDateTime.now().plusDays(2))
                .endBooking(LocalDateTime.now().plusDays(4))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        Booking savedApprovedBooking = bookingRepository.save(approvedBooking);

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> bookingService.approveBooking(owner.getId(), savedApprovedBooking.getId(), true));

        assertThat(exception.getMessage()).isEqualTo("Booking status has already been changed");

        Booking unchangedBooking = bookingRepository.findById(savedApprovedBooking.getId()).orElseThrow();
        assertThat(unchangedBooking.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void approveBooking_ShouldThrowBadRequestException_WhenRejectingAlreadyRejectedBooking() {
        Booking rejectedBooking = Booking.builder()
                .startBooking(LocalDateTime.now().plusDays(2))
                .endBooking(LocalDateTime.now().plusDays(4))
                .item(item)
                .booker(booker)
                .status(BookingStatus.REJECTED)
                .build();
        Booking savedRejectedBooking = bookingRepository.save(rejectedBooking);

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> bookingService.approveBooking(owner.getId(), savedRejectedBooking.getId(), false));

        assertThat(exception.getMessage()).isEqualTo("Booking status has already been changed");

        Booking unchangedBooking = bookingRepository.findById(savedRejectedBooking.getId()).orElseThrow();
        assertThat(unchangedBooking.getStatus()).isEqualTo(BookingStatus.REJECTED);
    }

    @Test
    void approveBooking_ShouldWorkWithMultipleBookingsForSameItem() {
        Booking anotherWaitingBooking = Booking.builder()
                .startBooking(LocalDateTime.now().plusDays(5))
                .endBooking(LocalDateTime.now().plusDays(7))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
        anotherWaitingBooking = bookingRepository.save(anotherWaitingBooking);

        BookingResponseDto result1 = bookingService.approveBooking(owner.getId(), waitingBooking.getId(), true);

        assertThat(result1.getStatus()).isEqualTo(BookingStatus.APPROVED);

        BookingResponseDto result2 = bookingService.approveBooking(owner.getId(), anotherWaitingBooking.getId(), false);

        assertThat(result2.getStatus()).isEqualTo(BookingStatus.REJECTED);

        Booking booking1 = bookingRepository.findById(waitingBooking.getId()).orElseThrow();
        Booking booking2 = bookingRepository.findById(anotherWaitingBooking.getId()).orElseThrow();

        assertThat(booking1.getStatus()).isEqualTo(BookingStatus.APPROVED);
        assertThat(booking2.getStatus()).isEqualTo(BookingStatus.REJECTED);
    }

    @Test
    void approveBooking_ShouldThrowBadRequestException_WhenOwnerTriesToApproveTwice() {
        bookingService.approveBooking(owner.getId(), waitingBooking.getId(), true);

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> bookingService.approveBooking(owner.getId(), waitingBooking.getId(), true));

        assertThat(exception.getMessage()).isEqualTo("Booking status has already been changed");
    }

    @Test
    void approveBooking_ShouldThrowBadRequestException_WhenOwnerTriesToChangeAfterRejection() {
        bookingService.approveBooking(owner.getId(), waitingBooking.getId(), false);

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> bookingService.approveBooking(owner.getId(), waitingBooking.getId(), true));

        assertThat(exception.getMessage()).isEqualTo("Booking status has already been changed");
    }

    @Test
    void approveBooking_ShouldPreserveAllFields_WhenApproving() {
        LocalDateTime originalStart = waitingBooking.getStartBooking();
        LocalDateTime originalEnd = waitingBooking.getEndBooking();
        Long originalBookerId = waitingBooking.getBooker().getId();
        Long originalItemId = waitingBooking.getItem().getId();

        BookingResponseDto result = bookingService.approveBooking(owner.getId(), waitingBooking.getId(), true);

        assertThat(result.getStart()).isEqualTo(originalStart);
        assertThat(result.getEnd()).isEqualTo(originalEnd);
        assertThat(result.getBooker().getId()).isEqualTo(originalBookerId);
        assertThat(result.getItem().getId()).isEqualTo(originalItemId);
        assertThat(result.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void approveBooking_ShouldHandleBookingFromDifferentUser_WhenOwnerHasMultipleItems() {
        Item secondItem = Item.builder()
                .name("Mouse")
                .description("Wireless mouse")
                .available(true)
                .owner(owner)
                .build();
        secondItem = itemRepository.save(secondItem);

        User anotherBooker = User.builder()
                .name("Another Booker")
                .email("another.booker@example.com")
                .build();
        anotherBooker = userRepository.save(anotherBooker);

        Booking secondBooking = Booking.builder()
                .startBooking(LocalDateTime.now().plusDays(1))
                .endBooking(LocalDateTime.now().plusDays(2))
                .item(secondItem)
                .booker(anotherBooker)
                .status(BookingStatus.WAITING)
                .build();
        secondBooking = bookingRepository.save(secondBooking);

        BookingResponseDto result = bookingService.approveBooking(owner.getId(), secondBooking.getId(), true);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(BookingStatus.APPROVED);
        assertThat(result.getBooker().getId()).isEqualTo(anotherBooker.getId());
        assertThat(result.getItem().getId()).isEqualTo(secondItem.getId());
    }
}