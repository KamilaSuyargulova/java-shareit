package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class ItemStorageImpl implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Collection<Item> getUsersItems(Long ownerId) {
        return items.values()
                .stream()
                .filter(e -> e.getOwner().getId().equals(ownerId))
                .toList();
    }

    @Override
    public Optional<Item> getItemById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public Collection<Item> getAvailableItemByText(String searchText) {
        return items.values()
                .stream()
                .filter(e -> (e.getName().equalsIgnoreCase(searchText)
                        || e.getDescription().equalsIgnoreCase(searchText))
                        && e.getAvailable().equals(true))
                .toList();
    }

    @Override
    public Item addItem(Item item) {
        item.setId(getNextId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item deleteItem(Long id) {
        return items.remove(id);
    }

    private Long getNextId() {
        long currentId = items.size();
        return ++currentId;
    }
}