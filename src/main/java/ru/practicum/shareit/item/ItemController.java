package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentRequestDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemBookingDto> getUsersItems(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Получить все предметы пользователя id = {} с датами бронирований", ownerId);
        return itemService.getUsersItemsWithBookings(ownerId);
    }

    @GetMapping("/{itemId}")
    public ItemBookingDto getItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @Positive @PathVariable(value = "itemId") Long itemId) {
        log.info("Получить предмет по id = {} для пользователя id = {}", itemId, userId);
        return itemService.getItemWithBookingsAndComments(itemId, userId);
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
        log.info("Добавить новый предмет для пользователя id = {}", ownerId);
        return itemService.addItem(ownerId, itemRequestDto);
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto updateItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                      @Positive @PathVariable(value = "itemId") Long itemId,
                                      @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Обновить предмет с id = {} для пользователя id = {}", itemId, ownerId);
        return itemService.updateItem(ownerId, itemId, itemRequestDto);
    }

    @DeleteMapping("/{itemId}")
    public ItemResponseDto deleteItemById(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                          @Positive @PathVariable(value = "itemId") Long itemId) {
        log.info("Удалить предмет с id = {} для пользователя id = {}", itemId, ownerId);
        return itemService.deleteItemById(ownerId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponseDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @Positive @PathVariable Long itemId,
                                         @Valid @RequestBody CommentRequestDto commentRequestDto) {
        log.info("Добавить комментарий к предмету id = {} от пользователя id = {}", itemId, userId);
        return itemService.addComment(userId, itemId, commentRequestDto);
    }
}