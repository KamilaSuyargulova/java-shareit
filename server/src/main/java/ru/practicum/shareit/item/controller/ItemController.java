package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemExtendedResponseDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static ru.practicum.shareit.common.Constants.HEADER_USER_ID;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/{itemId}")
    public ItemExtendedResponseDto getItem(@RequestHeader(HEADER_USER_ID) Long userId,
                                           @PathVariable Long itemId) {
        return itemService.getItem(userId, itemId);
    }

    @GetMapping()
    public List<ItemExtendedResponseDto> getUserItems(@RequestHeader(HEADER_USER_ID) Long userId) {
        return itemService.getUserItems(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponseDto createItem(@RequestHeader(HEADER_USER_ID) Long userId,
                                      @RequestBody ItemCreateDto itemCreateDto) {
        return itemService.createItem(userId, itemCreateDto);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponseDto createComment(@RequestHeader(HEADER_USER_ID) Long userId,
                                            @PathVariable Long itemId,
                                            @RequestBody CommentCreateDto commentCreateDto) {
        return itemService.createComment(userId, itemId, commentCreateDto);
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto updateItem(@RequestHeader(HEADER_USER_ID) Long userId,
                                      @PathVariable Long itemId,
                                      @RequestBody ItemUpdateDto itemUpdateDto) {
        return itemService.updateItem(userId, itemId, itemUpdateDto);
    }

    @GetMapping("/search")
    public List<ItemResponseDto> searchItems(@RequestHeader(HEADER_USER_ID) Long userId,
                                             @RequestParam(required = false) String text) {
        return itemService.searchItems(userId, text);
    }
}