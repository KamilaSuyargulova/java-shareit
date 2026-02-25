package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestExtendedResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemMapper itemMapper;

    public ItemRequestResponseDto createItemRequest(Long requestorId, ItemRequestCreateDto itemRequestCreateDto) {
        ItemRequest itemRequestToCreate = itemRequestMapper.mapToItemRequest(itemRequestCreateDto);
        itemRequestToCreate.setRequestor(userRepository.findById(requestorId).orElseThrow(
                () -> new NotFoundException("User with id=" + requestorId + " not found")
        ));
        itemRequestToCreate.setCreated(LocalDateTime.now());

        return itemRequestMapper.mapToResponseDto(itemRequestRepository.save(itemRequestToCreate));
    }

    public List<ItemRequestResponseDto> getAllItemRequest(Long userId) {
        return itemRequestRepository.findAllByRequestorIdNot(userId).stream()
                .map(itemRequestMapper::mapToResponseDto)
                .toList();
    }

    public List<ItemRequestExtendedResponseDto> getAllRequestorItemRequests(Long requestorId) {
        validateUserExists(requestorId);

        List<ItemRequest> allUserRequests = itemRequestRepository.findByRequestor_IdOrderByCreatedDesc(requestorId);

        if (allUserRequests.isEmpty()) {
            return List.of();
        }

        List<Item> items = itemRepository.findByRequestorIdWithDetails(requestorId);

        Map<Long, List<Item>> itemsByRequestId = items.stream()
                .collect(Collectors.groupingBy(
                        item -> item.getRequest().getId()
                ));

        return allUserRequests.stream()
                .map(request -> ItemRequestExtendedResponseDto.builder()
                        .id(request.getId())
                        .description(request.getDescription())
                        .created(request.getCreated())
                        .items(itemMapper.mapToShortResponseDtoForList(
                                itemsByRequestId.getOrDefault(request.getId(), List.of())
                        ))
                        .build())
                .collect(Collectors.toList());
    }

    public ItemRequestExtendedResponseDto getRequestById(Long requestId, Long userId) {
        validateUserExists(userId);

        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден"));

        List<Item> items = itemRepository.findByRequest_Id(requestId);

        return ItemRequestExtendedResponseDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .items(itemMapper.mapToShortResponseDtoForList(items))
                .build();
    }

    private void validateUserExists(Long requestorId) {
        if (!userRepository.existsById(requestorId)) {
            log.warn("User with id={} not found", requestorId);
            throw new NotFoundException("User with id=" + requestorId + " not found");
        }
    }
}