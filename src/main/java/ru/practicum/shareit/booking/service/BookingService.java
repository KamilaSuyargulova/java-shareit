package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;

import java.util.List;

public interface BookingService {
    BookingDtoOutput createBooking(Long userId, BookingDtoInput bookingDtoInput);

    BookingDtoOutput approveBooking(Long userId, Long bookingId, Boolean approved);

    BookingDtoOutput getBookingById(Long userId, Long bookingId);

    List<BookingDtoOutput> getUserBookings(Long userId, BookingState state);

    List<BookingDtoOutput> getOwnerBookings(Long ownerId, BookingState state);
}