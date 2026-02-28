package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.item.ItemClient;

import static ru.practicum.shareit.common.Constants.HEADER_USER_ID;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader(HEADER_USER_ID) @Positive Long userId,
                                          @PathVariable @Positive Long itemId) {
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping()
    public ResponseEntity<Object> getUserItems(@RequestHeader(HEADER_USER_ID) @Positive Long userId) {
        return itemClient.getUserItems(userId);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(HEADER_USER_ID) @Positive Long userId,
                                             @RequestBody @Valid ItemCreateDto itemCreateDto) {
        return itemClient.createItem(userId, itemCreateDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(HEADER_USER_ID) @Positive Long userId,
                                                @PathVariable @Positive Long itemId,
                                                @RequestBody @Valid CommentCreateDto commentCreateDto) {
        return itemClient.createComment(userId, itemId, commentCreateDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(HEADER_USER_ID) @Positive Long userId,
                                             @PathVariable @Positive Long itemId,
                                             @RequestBody @Valid ItemUpdateDto itemUpdateDto) {
        return itemClient.updateItem(userId, itemId, itemUpdateDto);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestHeader(HEADER_USER_ID) @Positive Long userId,
                                              @RequestParam(required = false) String text) {
        return itemClient.searchItem(userId, text);
    }
}