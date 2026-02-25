package ru.practicum.shareit.item.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemMapper {
    private final CommentMapper commentMapper;

    public ItemResponseDto mapToResponseDto(Item item) {
        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public List<ItemShortResponseDto> mapToShortResponseDtoForList(List<Item> items) {
        return items.stream()
                .map(this::mapToShortResponseDto)
                .collect(Collectors.toList());
    }

    public ItemShortResponseDto mapToShortResponseDto(Item item) {
        return ItemShortResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .build();
    }

    public ItemExtendedResponseDto mapToExtendedResponseDto(Item item) {
        return ItemExtendedResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .comments(item.getComments().stream()
                        .map(commentMapper::mapToResponseDto)
                        .toList())
                .build();
    }

    public Item mapToItem(ItemCreateDto itemCreateDto) {
        return Item.builder()
                .name(itemCreateDto.getName())
                .description(itemCreateDto.getDescription())
                .available(itemCreateDto.getAvailable())
                .build();
    }

    public Item mapToItem(ItemUpdateDto itemUpdateDto) {
        return Item.builder()
                .name(itemUpdateDto.getName())
                .description(itemUpdateDto.getDescription())
                .available(itemUpdateDto.getAvailable())
                .build();
    }
}