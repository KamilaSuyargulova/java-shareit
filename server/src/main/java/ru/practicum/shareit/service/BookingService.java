package ru.practicum.shareit.service;

import ru.practicum.shareit.dto.BookingCreateDto;
import ru.practicum.shareit.dto.BookingResponseDto;
import ru.practicum.shareit.model.BookingState;

import java.util.List;

public interface BookingService {
    BookingResponseDto getBooking(Long userId, Long bookingId);

    List<BookingResponseDto> getBookerBookings(Long userId, BookingState state);

    List<BookingResponseDto> getOwnerBookings(Long userId, BookingState state);

    BookingResponseDto createBooking(Long userId, BookingCreateDto bookingCreateDto);

    BookingResponseDto approveBooking(Long userId, Long bookingId, Boolean approved);
}