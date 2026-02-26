package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.dto.BookingCreateDto;
import ru.practicum.shareit.dto.BookingResponseDto;
import ru.practicum.shareit.mapper.BookingMapper;
import ru.practicum.shareit.model.Booking;
import ru.practicum.shareit.model.BookingState;
import ru.practicum.shareit.model.BookingStatus;
import ru.practicum.shareit.repository.BookingRepository;
import ru.practicum.shareit.service.BookingServiceImpl;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User owner;
    private User booker;
    private Item item;
    private Booking booking;
    private BookingCreateDto bookingCreateDto;
    private BookingResponseDto bookingResponseDto;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .id(1L)
                .name("Owner")
                .email("owner@test.com")
                .build();

        booker = User.builder()
                .id(2L)
                .name("Booker")
                .email("booker@test.com")
                .build();

        item = Item.builder()
                .id(1L)
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .owner(owner)
                .build();

        booking = Booking.builder()
                .id(1L)
                .startBooking(LocalDateTime.now().plusDays(1))
                .endBooking(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();

        bookingCreateDto = BookingCreateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        bookingResponseDto = BookingResponseDto.builder()
                .id(1L)
                .start(booking.getStartBooking())
                .end(booking.getEndBooking())
                .status(BookingStatus.WAITING)
                .booker(booker)
                .item(item)
                .build();
    }

    @Test
    void getBooking_ShouldReturnBooking_WhenUserIsOwner() {
        when(bookingRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(booking));
        when(bookingMapper.mapToResponseDto(booking)).thenReturn(bookingResponseDto);

        BookingResponseDto result = bookingService.getBooking(1L, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(BookingStatus.WAITING, result.getStatus());
    }

    @Test
    void getBooking_ShouldReturnBooking_WhenUserIsBooker() {
        when(bookingRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(booking));
        when(bookingMapper.mapToResponseDto(booking)).thenReturn(bookingResponseDto);

        BookingResponseDto result = bookingService.getBooking(2L, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getBooking_ShouldThrowNotFoundException_WhenBookingNotFound() {
        when(bookingRepository.findByIdWithDetails(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getBooking(1L, 99L));
    }

    @Test
    void getBooking_ShouldThrowConflictException_WhenUserIsNotOwnerOrBooker() {
        when(bookingRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(booking));

        assertThrows(ConflictException.class, () -> bookingService.getBooking(3L, 1L));
    }

    @Test
    void getBookerBookings_ShouldReturnBookings_WhenStateIsAll() {
        when(userRepository.existsById(2L)).thenReturn(true);
        when(bookingRepository.findAllByBookerId(2L)).thenReturn(List.of(booking));
        when(bookingMapper.mapToResponseDto(booking)).thenReturn(bookingResponseDto);

        List<BookingResponseDto> result = bookingService.getBookerBookings(2L, BookingState.ALL);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void getBookerBookings_ShouldThrowNotFoundException_WhenUserNotFound() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> bookingService.getBookerBookings(99L, BookingState.ALL));
    }

    @Test
    void createBooking_ShouldCreateBooking_WhenDataIsValid() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(itemRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(item));
        when(bookingMapper.mapToBooking(bookingCreateDto)).thenReturn(booking);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.mapToResponseDto(booking)).thenReturn(bookingResponseDto);

        BookingResponseDto result = bookingService.createBooking(2L, bookingCreateDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(BookingStatus.WAITING, result.getStatus());
    }

    @Test
    void createBooking_ShouldThrowBadRequestException_WhenEndDateIsBeforeStartDate() {
        BookingCreateDto invalidDto = BookingCreateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(1))
                .build();

        Booking invalidBooking = Booking.builder()
                .startBooking(invalidDto.getStart())
                .endBooking(invalidDto.getEnd())
                .build();

        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(itemRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(item));
        when(bookingMapper.mapToBooking(invalidDto)).thenReturn(invalidBooking);

        assertThrows(BadRequestException.class, () -> bookingService.createBooking(2L, invalidDto));
    }

    @Test
    void createBooking_ShouldThrowNotFoundException_WhenOwnerTriesToBookOwnItem() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(item));
        when(bookingMapper.mapToBooking(bookingCreateDto)).thenReturn(booking);

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(1L, bookingCreateDto));
    }

    @Test
    void createBooking_ShouldThrowBadRequestException_WhenItemIsNotAvailable() {
        item.setAvailable(false);

        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(itemRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(item));
        when(bookingMapper.mapToBooking(bookingCreateDto)).thenReturn(booking);

        assertThrows(BadRequestException.class, () -> bookingService.createBooking(2L, bookingCreateDto));
    }

    @Test
    void approveBooking_ShouldApproveBooking_WhenUserIsOwner() {
        when(bookingRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.mapToResponseDto(booking)).thenReturn(bookingResponseDto);

        BookingResponseDto result = bookingService.approveBooking(1L, 1L, true);

        assertNotNull(result);
        assertEquals(BookingStatus.APPROVED, booking.getStatus());
    }

    @Test
    void approveBooking_ShouldRejectBooking_WhenUserIsOwner() {
        when(bookingRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.mapToResponseDto(booking)).thenReturn(bookingResponseDto);

        BookingResponseDto result = bookingService.approveBooking(1L, 1L, false);

        assertNotNull(result);
        assertEquals(BookingStatus.REJECTED, booking.getStatus());
    }

    @Test
    void approveBooking_ShouldThrowBadRequestException_WhenUserIsNotOwner() {
        when(bookingRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(booking));

        assertThrows(BadRequestException.class, () -> bookingService.approveBooking(2L, 1L, true));
    }

    @Test
    void approveBooking_ShouldThrowBadRequestException_WhenBookingStatusIsNotWaiting() {
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(booking));

        assertThrows(BadRequestException.class, () -> bookingService.approveBooking(1L, 1L, true));
    }
}