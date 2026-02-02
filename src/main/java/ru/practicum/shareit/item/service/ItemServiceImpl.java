package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
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
    public Collection<ItemDto> getUsersItemsDto(Long ownerId) {
        userService.getUserDtoById(ownerId);
        return itemStorage.getUsersItems(ownerId)
                .stream()
                .map(ItemMapper::jpaToDto)
                .toList();
    }

    @Override
    public ItemDto getItemDtoById(Long itemId) {
        return ItemMapper.jpaToDto(itemStorage.getItemById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Предмет с таким id не найден")));
    }

    @Override
    public Collection<ItemDto> getAvailableItemsDtoByText(String searchText) {
        if (searchText.isBlank())
            return Collections.emptyList();
        return itemStorage.getAvailableItemByText(searchText)
                .stream()
                .map(ItemMapper::jpaToDto)
                .toList();
    }

    @Override
    public ItemDto addItem(Long ownerId, ItemDto itemDto) {
        Item newItem = ItemMapper.dtoToJpa(itemDto);
        userService.getUserDtoById(ownerId);
        newItem.setOwner(UserMapper.dtoToJpa(userService.getUserDtoById(ownerId)));
        return ItemMapper.jpaToDto(itemStorage.addItem(newItem));
    }

    @Override
    public ItemDto updateItem(Long ownerId, Long itemId, ItemDto itemDto) {
        Item itemToUpdate = getItemById(itemId);
        userService.getUserDtoById(ownerId);
        if (!itemToUpdate.getOwner().getId().equals(ownerId))
            throw new EntityNotFoundException("Пользователь не является владельцем предмета");
        if (Objects.nonNull(itemDto.getName())
                && !itemDto.getName().isBlank()
                && !itemToUpdate.getName().equals(itemDto.getName()))
            itemToUpdate.setName(itemDto.getName());
        if (Objects.nonNull(itemDto.getDescription())
                && !itemToUpdate.getDescription().isBlank()
                && !itemToUpdate.getDescription().equals(itemDto.getDescription()))
            itemToUpdate.setDescription(itemDto.getDescription());
        if (Objects.nonNull(itemDto.getAvailable())
                && !itemToUpdate.getAvailable().equals(itemDto.getAvailable()))
            itemToUpdate.setAvailable(itemDto.getAvailable());
        return ItemMapper.jpaToDto(itemToUpdate);
    }

    @Override
    public ItemDto deleteItemById(Long ownerId, Long itemId) {
        Item itemToDelete = getItemById(itemId);
        userService.getUserDtoById(ownerId);
        if (!itemToDelete.getOwner().getId().equals(ownerId))
            throw new EntityNotFoundException("Пользователь не является владельцем предмета");

        return ItemMapper.jpaToDto(itemStorage.deleteItem(itemId));
    }

    private Item getItemById(Long id) {
        return itemStorage.getItemById(id)
                .orElseThrow(() -> new EntityNotFoundException("Предмет с таким id не найден"));
    }
}