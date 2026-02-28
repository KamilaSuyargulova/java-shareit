package ru.practicum.shareit.request.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestExtendedResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;

@Component
public class ItemRequestMapper {
    public ItemRequestResponseDto mapToResponseDto(ItemRequest itemRequest) {
        return ItemRequestResponseDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();
    }

    public ItemRequestExtendedResponseDto mapToExtendedResponseDto(ItemRequest itemRequests) {
        return ItemRequestExtendedResponseDto.builder()
                .id(itemRequests.getId())
                .description(itemRequests.getDescription())
                .created(itemRequests.getCreated())
                .build();
    }

    public ItemRequest mapToItemRequest(ItemRequestCreateDto itemRequestCreateDto) {
        return ItemRequest.builder()
                .description(itemRequestCreateDto.getDescription())
                .build();
    }
}