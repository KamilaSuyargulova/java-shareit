package ru.practicum.shareit.booking.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.service.ItemService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@Qualifier("BookingRepositoryImpl")
public class BookingRepositoryImpl implements BookingRepository {

    private Map<Long, Booking> bookings = new HashMap<>();
    private Long currentId = 0L;
    private final ItemService itemService;

    public BookingRepositoryImpl(ItemService itemService) {
        this.itemService = itemService;
    }

    @Override
    public Booking create(Booking booking) {
        booking.setId(++currentId);
        bookings.put(booking.getId(), booking);
        return booking;
    }

    @Override
    public Booking update(Booking booking) {
        bookings.put(booking.getId(), booking);
        return booking;
    }

    @Override
    public Booking getById(Long bookingId) {
        return bookings.get(bookingId);
    }

    @Override
    public List<Booking> getAllByBooker(Long bookerId) {
        return bookings.values().stream()
                .filter(booking -> booking.getBookerId().equals(bookerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Booking> getAllByOwner(Long ownerId) {
        var ownerItems = itemService.getItemsByOwner(ownerId);
        List<Long> ownerItemIds = ownerItems.stream()
                .map(item -> item.getId())
                .collect(Collectors.toList());

        return bookings.values().stream()
                .filter(booking -> ownerItemIds.contains(booking.getItemId()))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteByItemId(Long itemId) {
        bookings.values().removeIf(booking -> booking.getItemId().equals(itemId));
    }
}