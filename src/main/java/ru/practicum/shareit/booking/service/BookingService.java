package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto create(BookingDto bookingDto, Long userId);

    BookingDto approve(Long bookingId, Boolean approved, Long userId);

    BookingDto getById(Long bookingId, Long userId);

    List<BookingDto> getAllByBooker(Long userId, String state, Integer from, Integer size);

    List<BookingDto> getAllByOwner(Long userId, String state, Integer from, Integer size);
}