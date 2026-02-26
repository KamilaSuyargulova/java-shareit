package ru.practicum.shareit.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.dto.BookingCreateDto;
import ru.practicum.shareit.dto.BookingResponseDto;
import ru.practicum.shareit.model.BookingState;
import ru.practicum.shareit.service.BookingServiceImpl;

import java.util.List;

import static ru.practicum.shareit.common.Constants.HEADER_USER_ID;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingServiceImpl bookingServiceImpl;

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBooking(@RequestHeader(HEADER_USER_ID) Long userId,
                                         @PathVariable Long bookingId) {
        return bookingServiceImpl.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponseDto> getBookerBookings(@RequestHeader(HEADER_USER_ID) Long userId,
                                                      @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingServiceImpl.getBookerBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getOwnerBookings(@RequestHeader(HEADER_USER_ID) Long userId,
                                                     @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingServiceImpl.getOwnerBookings(userId, state);
    }

    @PostMapping
    public BookingResponseDto createBooking(@RequestHeader(HEADER_USER_ID) Long userId,
                                            @RequestBody BookingCreateDto bookingCreateDto) {
        return bookingServiceImpl.createBooking(userId, bookingCreateDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approveBooking(@RequestHeader(HEADER_USER_ID) Long userId,
                                             @PathVariable Long bookingId,
                                             @RequestParam Boolean approved) {
        return bookingServiceImpl.approveBooking(userId, bookingId, approved);
    }
}