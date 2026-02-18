package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class BookingMapper {

    public static Booking toBooking(BookingDtoInput bookingDtoInput, Item item, User booker) {
        Booking booking = new Booking();
        booking.setStart(bookingDtoInput.getStart());
        booking.setEnd(bookingDtoInput.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        return booking;
    }

    public static BookingDto toBookingDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem() != null ? booking.getItem().getId() : null,
                booking.getBooker() != null ? booking.getBooker().getId() : null,
                booking.getStatus()
        );
    }

    public static BookingDtoOutput toBookingDtoOutput(Booking booking) {
        ItemDto itemDto = ItemMapper.toItemDto(booking.getItem());
        UserDto bookerDto = UserMapper.toUserDto(booking.getBooker());

        return new BookingDtoOutput(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                itemDto,
                bookerDto,
                booking.getStatus()
        );
    }

    public static BookingDto toBookingDtoWithDates(LocalDateTime start, LocalDateTime end) {
        return new BookingDto(start, end);
    }
}