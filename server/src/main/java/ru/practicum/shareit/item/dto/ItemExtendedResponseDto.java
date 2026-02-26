package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.dto.BookingShortResponseDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;

import java.util.List;

@Data
@Builder
public class ItemExtendedResponseDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingShortResponseDto lastBooking;
    private BookingShortResponseDto nextBooking;
    private List<CommentResponseDto> comments;
}