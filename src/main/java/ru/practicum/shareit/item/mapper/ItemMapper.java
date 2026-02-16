package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {

    public static Item requestDtoToJpa(ItemRequestDto itemRequestDto) {
        return Item.builder()
                .name(itemRequestDto.getName())
                .description(itemRequestDto.getDescription())
                .available(itemRequestDto.getAvailable())
                .request(itemRequestDto.getRequest())
                .build();
    }

    public static ItemResponseDto jpaToResponseDto(Item item) {
        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .request(item.getRequest())
                .build();
    }
}