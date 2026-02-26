package ru.practicum.shareit.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingCreateDto {
    private Long itemId;

    private LocalDateTime start;

    private LocalDateTime end;
}