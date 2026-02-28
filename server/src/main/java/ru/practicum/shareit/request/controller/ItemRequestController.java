package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestExtendedResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static ru.practicum.shareit.common.Constants.HEADER_USER_ID;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestResponseDto createItemRequest(@RequestHeader(HEADER_USER_ID) Long requestorId,
                                                    @RequestBody ItemRequestCreateDto itemRequestCreateDto) {
        return itemRequestService.createItemRequest(requestorId, itemRequestCreateDto);
    }

    @GetMapping("/all")
    public List<ItemRequestResponseDto> getAllItemRequests(@RequestHeader(HEADER_USER_ID) Long userId) {
        return itemRequestService.getAllItemRequest(userId);
    }

    @GetMapping
    public List<ItemRequestExtendedResponseDto> getAllRequestorItemRequests(@RequestHeader(HEADER_USER_ID) Long requestorId) {
        return itemRequestService.getAllRequestorItemRequests(requestorId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestExtendedResponseDto getItemRequestById(@RequestHeader(HEADER_USER_ID) Long userId,
                                                             @PathVariable Long requestId) {
        return itemRequestService.getRequestById(requestId, userId);
    }
}