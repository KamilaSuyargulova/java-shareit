package ru.practicum.shareit.booking.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;

@Data
@RequiredArgsConstructor
public class Booking {
    private Long id;
    @NotNull
    @FutureOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDate start;
    @NotNull
    @Future
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDate end;
    @NotNull
    private Item item;
    private User booker;
    @NotNull
    private BookingStatus bookingStatus;
}