package ru.practicum.shareit.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingShortResponseDto {
    private Long id;
    private Long bookerId;
}