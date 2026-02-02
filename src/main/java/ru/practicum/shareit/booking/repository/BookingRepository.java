package ru.practicum.shareit.booking.repository;

import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingRepository {
    Booking create(Booking booking);

    Booking update(Booking booking);

    Booking getById(Long bookingId);

    List<Booking> getAllByBooker(Long bookerId);

    List<Booking> getAllByOwner(Long ownerId);

    void deleteByItemId(Long itemId);
}