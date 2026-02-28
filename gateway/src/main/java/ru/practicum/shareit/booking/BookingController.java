package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.dto.BookingCreateDto;
import ru.practicum.shareit.dto.BookingState;

import static ru.practicum.shareit.common.Constants.HEADER_USER_ID;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(HEADER_USER_ID) @Positive Long userId,
                                             @PathVariable @Positive Long bookingId) {
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookerBookings(@RequestHeader(HEADER_USER_ID) @Positive Long userId,
                                                    @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingClient.getBookerBookings(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookings(@RequestHeader(HEADER_USER_ID) @Positive Long userId,
                                                   @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingClient.getOwnerBookings(userId, state);
    }

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader(HEADER_USER_ID) @Positive Long userId,
                                                @RequestBody @Valid BookingCreateDto bookingCreateDto) {
        return bookingClient.createBooking(userId, bookingCreateDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader(HEADER_USER_ID) @Positive Long userId,
                                                 @PathVariable @Positive Long bookingId,
                                                 @RequestParam Boolean approved) {
        return bookingClient.approveBooking(userId, bookingId, approved);
    }
}