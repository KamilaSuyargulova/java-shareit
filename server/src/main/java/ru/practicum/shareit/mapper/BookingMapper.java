package ru.practicum.shareit.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.dto.BookingCreateDto;
import ru.practicum.shareit.dto.BookingResponseDto;
import ru.practicum.shareit.dto.BookingShortResponseDto;
import ru.practicum.shareit.model.Booking;

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