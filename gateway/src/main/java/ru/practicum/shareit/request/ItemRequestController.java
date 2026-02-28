package ru.practicum.shareit.request;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

import static ru.practicum.shareit.common.Constants.HEADER_USER_ID;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader(HEADER_USER_ID) @Positive long requestorId,
                                                    @RequestBody ItemRequestCreateDto itemRequestCreateDto) {
        return itemRequestClient.createRequest(requestorId, itemRequestCreateDto);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(@RequestHeader(HEADER_USER_ID) @Positive long userId) {
        return itemRequestClient.getAllItemRequest(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllRequestorItemRequests(@RequestHeader(HEADER_USER_ID) @Positive long requestorId) {
        return itemRequestClient.getAllRequestorItemRequests(requestorId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@RequestHeader(HEADER_USER_ID) @Positive long userId,
                                                     @PathVariable @Positive long requestId) {
        return itemRequestClient.getItemRequestById(userId, requestId);
    }
}