package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ShareItApp.class)
@ActiveProfiles("test")
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingRepositoryTest {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private User owner;
    private User booker;
    private Item item;
    private Booking booking1;
    private Booking booking2;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();

        owner = User.builder()
                .name("Owner")
                .email("owner@test.com")
                .build();
        owner = userRepository.save(owner);

        booker = User.builder()
                .name("Booker")
                .email("booker@test.com")
                .build();
        booker = userRepository.save(booker);

        item = Item.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(owner)
                .build();
        item = itemRepository.save(item);

        LocalDateTime now = LocalDateTime.now();

        booking1 = Booking.builder()
                .startBooking(now.plusDays(1))
                .endBooking(now.plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();

        booking2 = Booking.builder()
                .startBooking(now.plusDays(3))
                .endBooking(now.plusDays(4))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();

        bookingRepository.saveAll(List.of(booking1, booking2));
    }

    @Test
    void findByIdWithDetails_ShouldReturnBookingWithFetchedEntities() {
        var found = bookingRepository.findByIdWithDetails(booking1.getId());

        assertThat(found).isPresent();
        Booking booking = found.get();

        assertThat(booking.getBooker()).isNotNull();
        assertThat(booking.getBooker().getName()).isEqualTo("Booker");
        assertThat(booking.getItem()).isNotNull();
        assertThat(booking.getItem().getName()).isEqualTo("Test Item");
        assertThat(booking.getItem().getOwner()).isNotNull();
        assertThat(booking.getItem().getOwner().getName()).isEqualTo("Owner");
    }

    @Test
    void findAllByBookerId_ShouldReturnAllBookingsForBooker() {
        var bookings = bookingRepository.findAllByBookerId(booker.getId());

        assertThat(bookings).hasSize(2);
        assertThat(bookings.get(0).getBooker().getId()).isEqualTo(booker.getId());
    }

    @Test
    void findCurrentByBookerId_ShouldReturnCurrentBookings() {
        LocalDateTime now = LocalDateTime.now();

        Booking currentBooking = Booking.builder()
                .startBooking(now.minusHours(1))
                .endBooking(now.plusHours(1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        bookingRepository.save(currentBooking);

        var bookings = bookingRepository.findCurrentByBookerId(booker.getId(), now);

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getId()).isEqualTo(currentBooking.getId());
        assertThat(bookings.get(0).getStartBooking()).isBefore(now);
        assertThat(bookings.get(0).getEndBooking()).isAfter(now);
    }

    @Test
    void findPastByBookerId_ShouldReturnPastBookings() {
        LocalDateTime now = LocalDateTime.now();

        Booking pastBooking = Booking.builder()
                .startBooking(now.minusDays(2))
                .endBooking(now.minusDays(1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        bookingRepository.save(pastBooking);

        var bookings = bookingRepository.findPastByBookerId(booker.getId(), now);

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getId()).isEqualTo(pastBooking.getId());
        assertThat(bookings.get(0).getEndBooking()).isBefore(now);
    }

    @Test
    void findFutureByBookerId_ShouldReturnFutureBookings() {
        LocalDateTime now = LocalDateTime.now();

        var bookings = bookingRepository.findFutureByBookerId(booker.getId(), now);

        assertThat(bookings).hasSize(2);
        assertThat(bookings.get(0).getStartBooking()).isAfter(now);
        assertThat(bookings.get(1).getStartBooking()).isAfter(now);
    }

    @Test
    void findByBookerIdAndStatus_ShouldReturnBookingsWithGivenStatus() {
        var waitingBookings = bookingRepository.findByBookerIdAndStatus(booker.getId(), BookingStatus.WAITING);

        assertThat(waitingBookings).hasSize(1);
        assertThat(waitingBookings.get(0).getStatus()).isEqualTo(BookingStatus.WAITING);

        var approvedBookings = bookingRepository.findByBookerIdAndStatus(booker.getId(), BookingStatus.APPROVED);
        assertThat(approvedBookings).hasSize(1);
        assertThat(approvedBookings.get(0).getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void findAllByOwnerId_ShouldReturnAllBookingsForOwner() {
        var bookings = bookingRepository.findAllByOwnerId(owner.getId());

        assertThat(bookings).hasSize(2);
        assertThat(bookings.get(0).getItem().getOwner().getId()).isEqualTo(owner.getId());
    }

    @Test
    void findCurrentByOwnerId_ShouldReturnCurrentBookingsForOwner() {
        LocalDateTime now = LocalDateTime.now();

        Booking currentBooking = Booking.builder()
                .startBooking(now.minusHours(1))
                .endBooking(now.plusHours(1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        bookingRepository.save(currentBooking);

        var bookings = bookingRepository.findCurrentByOwnerId(owner.getId(), now);

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getId()).isEqualTo(currentBooking.getId());
    }

    @Test
    void findPastByOwnerId_ShouldReturnPastBookingsForOwner() {
        LocalDateTime now = LocalDateTime.now();

        Booking pastBooking = Booking.builder()
                .startBooking(now.minusDays(2))
                .endBooking(now.minusDays(1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        bookingRepository.save(pastBooking);

        var bookings = bookingRepository.findPastByOwnerId(owner.getId(), now);

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getId()).isEqualTo(pastBooking.getId());
    }

    @Test
    void findFutureByOwnerId_ShouldReturnFutureBookingsForOwner() {
        LocalDateTime now = LocalDateTime.now();

        var bookings = bookingRepository.findFutureByOwnerId(owner.getId(), now);

        assertThat(bookings).hasSize(2);
        assertThat(bookings.get(0).getStartBooking()).isAfter(now);
    }

    @Test
    void findByOwnerIdAndStatus_ShouldReturnOwnerBookingsWithGivenStatus() {
        var waitingBookings = bookingRepository.findByOwnerIdAndStatus(owner.getId(), BookingStatus.WAITING);

        assertThat(waitingBookings).hasSize(1);
        assertThat(waitingBookings.get(0).getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void existsByBookerIdAndItemIdAndStatusAndEndBookingBefore_ShouldReturnTrue_WhenExists() {
        LocalDateTime now = LocalDateTime.now();

        Booking pastBooking = Booking.builder()
                .startBooking(now.minusDays(2))
                .endBooking(now.minusDays(1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        bookingRepository.save(pastBooking);

        boolean exists = bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBookingBefore(
                booker.getId(), item.getId(), BookingStatus.APPROVED, now);

        assertThat(exists).isTrue();
    }

    @Test
    void existsByBookerIdAndItemIdAndStatusAndEndBookingBefore_ShouldReturnFalse_WhenNotExists() {
        LocalDateTime now = LocalDateTime.now();

        boolean exists = bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBookingBefore(
                booker.getId(), item.getId(), BookingStatus.APPROVED, now);

        assertThat(exists).isFalse();
    }

    @Test
    void findLastBookingsForItems_ShouldReturnLastBookings() {
        LocalDateTime now = LocalDateTime.now();

        Booking oldBooking = Booking.builder()
                .startBooking(now.minusDays(5))
                .endBooking(now.minusDays(4))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        bookingRepository.save(oldBooking);

        Booking lastBooking = Booking.builder()
                .startBooking(now.minusDays(2))
                .endBooking(now.minusDays(1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        bookingRepository.save(lastBooking);

        var lastBookings = bookingRepository.findLastBookingsForItems(List.of(item.getId()), now);

        assertThat(lastBookings).hasSize(1);
        assertThat(lastBookings.get(0).getId()).isEqualTo(lastBooking.getId());
    }

}