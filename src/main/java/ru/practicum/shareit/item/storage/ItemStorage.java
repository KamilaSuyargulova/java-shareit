package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemStorage {

    Collection<Item> getUsersItems(Long ownerId);

    Optional<Item> getItemById(Long id);

    Collection<Item> getAvailableItemByText(String searchText);

    Item addItem(Item item);

    Item deleteItem(Long id);
}