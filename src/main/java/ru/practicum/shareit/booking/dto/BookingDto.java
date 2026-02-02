package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;

public class BookingDto {
    private Long id;
    private LocalDate start;
    private LocalDate end;
    private Item item;
    private User booker;
    private BookingStatus bookingStatus;
}