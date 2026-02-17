package ru.practicum.shareit.item.service;

import jakarta.validation.constraints.Positive;
import ru.practicum.shareit.comment.dto.CommentRequestDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.Collection;
import java.util.List;

public interface ItemService {

    List<ItemBookingDto> getUsersItemsWithBookings(Long ownerId);

    ItemBookingDto getItemWithBookingsAndComments(Long itemId, Long userId);

    Collection<ItemResponseDto> getAvailableItemsDtoByText(@Positive String searchText);

    ItemResponseDto addItem(Long ownerId, ItemRequestDto itemRequestDto);

    ItemResponseDto updateItem(Long ownerId, Long itemId, ItemRequestDto itemRequestDto);

    ItemResponseDto deleteItemById(Long ownerId, Long itemId);

    CommentResponseDto addComment(Long userId, Long itemId, CommentRequestDto commentRequestDto);
}