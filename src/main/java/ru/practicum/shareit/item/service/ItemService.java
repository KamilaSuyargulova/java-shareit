package ru.practicum.shareit.item.service;

import jakarta.validation.constraints.Positive;
import ru.practicum.shareit.item.dto.*;

import java.util.Collection;

public interface ItemService {

    Collection<ItemResponseDto> getUsersItemsDto(Long ownerId);

    ItemResponseDto getItemDtoById(Long id);

    Collection<ItemResponseDto> getAvailableItemsDtoByText(@Positive String searchText);

    ItemResponseDto addItem(Long ownerId, ItemRequestDto itemRequestDto);

    ItemResponseDto updateItem(Long ownerId, Long itemId, ItemRequestDto itemRequestDto);

    ItemResponseDto deleteItemById(Long ownerId, Long itemId);
}