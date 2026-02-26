package ru.practicum.shareit.booking.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingShortResponseDto;
import ru.practicum.shareit.booking.model.Booking;

@Component
public class BookingMapper {
    public BookingResponseDto mapToResponseDto(Booking booking) {
        return BookingResponseDto.builder()
                .id(booking.getId())
                .start(booking.getStartBooking())
                .end(booking.getEndBooking())
                .status(booking.getStatus())
                .booker(booking.getBooker())
                .item(booking.getItem())
                .build();
    }

    public BookingShortResponseDto mapToShortDto(Booking booking) {
        return BookingShortResponseDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }

    public Booking mapToBooking(BookingCreateDto bookingCreateDto) {
        return Booking.builder()
                .startBooking(bookingCreateDto.getStart())
                .endBooking(bookingCreateDto.getEnd())
                .build();
    }
}