package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingShortResponseDto;
import ru.practicum.shareit.booking.model.Booking;

public class BookingMapper {

    private BookingMapper() {
    }

    public static BookingResponseDto mapToResponseDto(Booking booking) {
        if (booking == null) {
            return null;
        }

        return BookingResponseDto.builder()
                .id(booking.getId())
                .start(booking.getStartBooking())
                .end(booking.getEndBooking())
                .status(booking.getStatus())
                .booker(booking.getBooker())
                .item(booking.getItem())
                .build();
    }

    public static BookingShortResponseDto mapToShortDto(Booking booking) {
        if (booking == null) {
            return null;
        }

        return BookingShortResponseDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }

    public static Booking mapToBooking(BookingCreateDto bookingCreateDto) {
        if (bookingCreateDto == null) {
            return null;
        }

        return Booking.builder()
                .startBooking(bookingCreateDto.getStart())
                .endBooking(bookingCreateDto.getEnd())
                .build();
    }
}