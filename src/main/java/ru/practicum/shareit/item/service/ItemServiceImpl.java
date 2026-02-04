package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;

    @Override
    public Collection<ItemResponseDto> getUsersItemsDto(Long ownerId) {
        userService.getUserDtoById(ownerId);
        return itemStorage.getUsersItems(ownerId)
                .stream()
                .map(ItemMapper::jpaToResponseDto)
                .toList();
    }

    @Override
    public ItemResponseDto getItemDtoById(Long itemId) {
        return ItemMapper.jpaToResponseDto(itemStorage.getItemById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Предмет с таким id не найден")));
    }

    @Override
    public Collection<ItemResponseDto> getAvailableItemsDtoByText(String searchText) {
        if (searchText.isBlank())
            return Collections.emptyList();
        return itemStorage.getAvailableItemByText(searchText)
                .stream()
                .map(ItemMapper::jpaToResponseDto)
                .toList();
    }

    @Override
    public ItemResponseDto addItem(Long ownerId, ItemRequestDto itemRequestDto) {
        Item newItem = ItemMapper.requestDtoToJpa(itemRequestDto);
        userService.getUserDtoById(ownerId);
        newItem.setOwner(UserMapper.dtoToJpa(userService.getUserDtoById(ownerId)));
        return ItemMapper.jpaToResponseDto(itemStorage.addItem(newItem));
    }

    @Override
    public ItemResponseDto updateItem(Long ownerId, Long itemId, ItemRequestDto itemRequestDto) {
        Item itemToUpdate = getItemById(itemId);
        userService.getUserDtoById(ownerId);
        if (!itemToUpdate.getOwner().getId().equals(ownerId))
            throw new EntityNotFoundException("У пользователя нет прав для изменения этого предмета");

        if (Objects.nonNull(itemRequestDto.getName())
                && !itemRequestDto.getName().isBlank()
                && !itemToUpdate.getName().equals(itemRequestDto.getName()))
            itemToUpdate.setName(itemRequestDto.getName());
        if (Objects.nonNull(itemRequestDto.getDescription())
                && !itemToUpdate.getDescription().isBlank()
                && !itemToUpdate.getDescription().equals(itemRequestDto.getDescription()))
            itemToUpdate.setDescription(itemRequestDto.getDescription());
        if (Objects.nonNull(itemRequestDto.getAvailable())
                && !itemToUpdate.getAvailable().equals(itemRequestDto.getAvailable()))
            itemToUpdate.setAvailable(itemRequestDto.getAvailable());
        return ItemMapper.jpaToResponseDto(itemToUpdate);
    }

    @Override
    public ItemResponseDto deleteItemById(Long ownerId, Long itemId) {
        Item itemToDelete = getItemById(itemId);
        userService.getUserDtoById(ownerId);
        if (!itemToDelete.getOwner().getId().equals(ownerId))
            throw new EntityNotFoundException("У пользователя нет прав для удаления этого предмета");

        return ItemMapper.jpaToResponseDto(itemStorage.deleteItem(itemId));
    }

    private Item getItemById(Long id) {
        return itemStorage.getItemById(id)
                .orElseThrow(() -> new EntityNotFoundException("Предмет с таким id не найден"));
    }
}