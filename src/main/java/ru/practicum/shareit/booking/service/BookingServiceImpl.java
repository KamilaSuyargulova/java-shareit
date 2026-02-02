package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemService itemService;
    private final UserService userService;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository,
                              ItemService itemService,
                              UserService userService) {
        this.bookingRepository = bookingRepository;
        this.itemService = itemService;
        this.userService = userService;
    }

    @Override
    public BookingDto create(BookingDto bookingDto, Long userId) {
        userService.getUserById(userId);

        var itemDto = itemService.getItemById(bookingDto.getItemId());
        if (itemDto == null) {
            throw new NotFoundException("Item with ID = " + bookingDto.getItemId() + " not found.");
        }

        if (itemDto.getOwnerId() != null && itemDto.getOwnerId().equals(userId)) {
            throw new NotFoundException("Owner cannot book his own item.");
        }

        if (!itemDto.getAvailable()) {
            throw new ValidationException("Item is not available for booking.");
        }

        if (bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            throw new ValidationException("Start and end dates must be specified.");
        }

        if (bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Start date cannot be in the past.");
        }

        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new ValidationException("End date must be after start date.");
        }

        if (bookingDto.getEnd().isEqual(bookingDto.getStart())) {
            throw new ValidationException("End date cannot be equal to start date.");
        }

        Booking booking = new Booking();
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItemId(bookingDto.getItemId());
        booking.setBookerId(userId);
        booking.setStatus("WAITING");

        Booking savedBooking = bookingRepository.create(booking);
        return BookingMapper.toBookingDto(savedBooking);
    }

    @Override
    public BookingDto approve(Long bookingId, Boolean approved, Long userId) {
        Booking booking = bookingRepository.getById(bookingId);
        if (booking == null) {
            throw new NotFoundException("Booking with ID = " + bookingId + " not found.");
        }

        var itemDto = itemService.getItemById(booking.getItemId());
        if (!itemDto.getOwnerId().equals(userId)) {
            throw new NotFoundException("Only item owner can approve booking.");
        }

        if (!booking.getStatus().equals("WAITING")) {
            throw new ValidationException("Booking status is already " + booking.getStatus());
        }

        booking.setStatus(approved ? "APPROVED" : "REJECTED");
        Booking updatedBooking = bookingRepository.update(booking);

        return BookingMapper.toBookingDto(updatedBooking);
    }

    @Override
    public BookingDto getById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.getById(bookingId);
        if (booking == null) {
            throw new NotFoundException("Booking with ID = " + bookingId + " not found.");
        }

        var itemDto = itemService.getItemById(booking.getItemId());
        if (!booking.getBookerId().equals(userId) && !itemDto.getOwnerId().equals(userId)) {
            throw new NotFoundException("User does not have access to this booking.");
        }

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getAllByBooker(Long userId, String state, Integer from, Integer size) {
        userService.getUserById(userId);

        List<Booking> bookings = bookingRepository.getAllByBooker(userId);

        List<Booking> filteredBookings = filterBookingsByState(bookings, state);

        List<Booking> paginatedBookings = filteredBookings.stream()
                .skip(from)
                .limit(size)
                .collect(Collectors.toList());

        return paginatedBookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getAllByOwner(Long userId, String state, Integer from, Integer size) {
        userService.getUserById(userId);

        List<Booking> bookings = bookingRepository.getAllByOwner(userId);

        List<Booking> filteredBookings = filterBookingsByState(bookings, state);

        List<Booking> paginatedBookings = filteredBookings.stream()
                .skip(from)
                .limit(size)
                .collect(Collectors.toList());

        return paginatedBookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private List<Booking> filterBookingsByState(List<Booking> bookings, String state) {
        LocalDateTime now = LocalDateTime.now();

        return bookings.stream()
                .filter(booking -> {
                    switch (state.toUpperCase()) {
                        case "CURRENT":
                            return booking.getStart().isBefore(now) && booking.getEnd().isAfter(now);
                        case "PAST":
                            return booking.getEnd().isBefore(now);
                        case "FUTURE":
                            return booking.getStart().isAfter(now);
                        case "WAITING":
                            return booking.getStatus().equals("WAITING");
                        case "REJECTED":
                            return booking.getStatus().equals("REJECTED");
                        case "ALL":
                            return true;
                        default:
                            throw new ValidationException("Unknown state: " + state);
                    }
                })
                .collect(Collectors.toList());
    }
}