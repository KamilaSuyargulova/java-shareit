package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemShortResponseDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ItemRequestExtendedResponseDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    private List<ItemShortResponseDto> items;
}