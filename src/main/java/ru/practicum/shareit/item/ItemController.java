package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public Collection<ItemResponseDto> getUsersItems(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Получить все предметы");
        return itemService.getUsersItemsDto(ownerId);
    }

    @GetMapping("/{itemId}")
    public ItemResponseDto getItemById(@Positive @PathVariable(value = "itemId") Long id) {
        log.info("Получить предмет по id = {}", id);
        return itemService.getItemDtoById(id);
    }

    @GetMapping("/search")
    public Collection<ItemResponseDto> getItemsByName(@RequestParam(name = "text") String searchText) {
        log.info("Получить предметы, содержащие строку '{}'", searchText);
        return itemService.getAvailableItemsDtoByText(searchText);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponseDto addNewItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                      @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Добавить новый предмет");
        return itemService.addItem(ownerId, itemRequestDto);
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto updateItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                      @Positive @PathVariable(value = "itemId") Long itemId,
                                      @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Обновить предмет с id = {}", itemId);
        return itemService.updateItem(ownerId, itemId, itemRequestDto);
    }

    @DeleteMapping("/{itemId}")
    public ItemResponseDto deleteItemById(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                          @Positive @PathVariable(value = "itemId") Long itemId) {
        log.info("Удалить предмет с id = {}", itemId);
        return itemService.deleteItemById(ownerId, itemId);
    }
}