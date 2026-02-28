package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemExtendedResponseDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

public interface ItemService {

    ItemExtendedResponseDto getItem(Long userId, Long itemId);

    ItemResponseDto createItem(Long userId, ItemCreateDto itemCreateDto);

    ItemResponseDto updateItem(Long userId, Long itemId, ItemUpdateDto itemUpdateDto);

    CommentResponseDto createComment(Long userId, Long itemId, CommentCreateDto commentCreateDto);

    List<ItemExtendedResponseDto> getUserItems(Long userId);

    List<ItemResponseDto> searchItems(Long userId, String searchQuery);
}