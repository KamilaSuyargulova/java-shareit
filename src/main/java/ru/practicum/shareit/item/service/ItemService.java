package ru.practicum.shareit.item.service;

import jakarta.validation.constraints.Positive;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {

    Collection<ItemDto> getUsersItemsDto(Long ownerId);

    ItemDto getItemDtoById(Long id);

    Collection<ItemDto> getAvailableItemsDtoByText(@Positive String searchText);

    ItemDto addItem(Long ownerId, ItemDto itemDto);

    ItemDto updateItem(Long ownerId, Long itemId, ItemDto itemDto);

    ItemDto deleteItemById(Long ownerId, Long itemId);
}