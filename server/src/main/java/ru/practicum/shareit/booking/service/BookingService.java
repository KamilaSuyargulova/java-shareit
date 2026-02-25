package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {
    BookingResponseDto getBooking(Long userId, Long bookingId);

    List<BookingResponseDto> getBookerBookings(Long userId, BookingState state);

    List<BookingResponseDto> getOwnerBookings(Long userId, BookingState state);

    BookingResponseDto createBooking(Long userId, BookingCreateDto bookingCreateDto);

    BookingResponseDto approveBooking(Long userId, Long bookingId, Boolean approved);
}