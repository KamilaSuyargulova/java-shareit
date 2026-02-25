package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemExtendedResponseDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;
    private final ItemMapper itemMapper;

    @Override
    @Transactional(readOnly = true)
    public ItemExtendedResponseDto getItem(Long userId, Long itemId) {
        log.info("User with id={} getting item by id={}", userId, itemId);

        Item existingItem = itemRepository.findByIdWithDetails(itemId).orElseThrow(
                () -> new NotFoundException("Item with id=" + itemId + " not found")
        );
        ItemExtendedResponseDto itemExtendedResponseDto = itemMapper.mapToExtendedResponseDto(existingItem);

        if (existingItem.getOwner().getId().equals(userId)) {
            enrichWithBookings(itemExtendedResponseDto, List.of(existingItem.getId()));
        }

        return itemExtendedResponseDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemExtendedResponseDto> getUserItems(Long userId) {
        log.info("User with id={} getting his items", userId);

        List<Item> items = itemRepository.findByOwnerIdWithDetails(userId);

        if (items.isEmpty()) {
            return Collections.emptyList();
        }

        List<ItemExtendedResponseDto> itemExtendedResponseDtos = items.stream()
                .map(itemMapper::mapToExtendedResponseDto)
                .collect(Collectors.toList());

        List<Long> itemIds = items.stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        enrichWithBookings(itemExtendedResponseDtos, itemIds);

        return itemExtendedResponseDtos;
    }

    @Override
    @Transactional
    public ItemResponseDto createItem(Long userId, ItemCreateDto itemCreateDto) {
        User owner = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User not found with id: " + userId)
        );

        Item itemToCreate = itemMapper.mapToItem(itemCreateDto);
        itemToCreate.setOwner(owner);

        if (itemCreateDto.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(itemCreateDto.getRequestId()).orElseThrow(
                    () -> new NotFoundException("Request not found with id: " + itemCreateDto.getRequestId())
            );
            itemToCreate.setRequest(itemRequest);
        }

        Item createdItem = itemRepository.save(itemToCreate);
        return itemMapper.mapToResponseDto(createdItem);
    }

    @Override
    @Transactional
    public CommentResponseDto createComment(Long userId, Long itemId, CommentCreateDto commentCreateDto) {
        log.info("User with id={} creating comment for item with id={}", userId, itemId);

        if (!userRepository.existsById(userId)) {
            log.warn("User with id={} not found", userId);
            throw new NotFoundException("User with id=" + userId + " not found");
        }
        if (!itemRepository.existsById(itemId)) {
            log.warn("Item with id={} not found", itemId);
            throw new NotFoundException("User with id=" + itemId + " not found");
        }

        boolean hasFinishedBooking = bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBookingBefore(
                userId,
                itemId,
                BookingStatus.APPROVED,
                LocalDateTime.now()
        );

        if (!hasFinishedBooking) {
            throw new BadRequestException(
                    "User has no completed booking for this item"
            );
        }

        Comment commentToCreate = commentMapper.mapToComment(commentCreateDto);
        commentToCreate.setItem(itemRepository.findById(itemId).orElseThrow());
        commentToCreate.setAuthor(userRepository.findById(userId).orElseThrow());
        commentToCreate.setCreated(LocalDateTime.now());

        Comment createdComment = commentRepository.save(commentToCreate);
        return commentMapper.mapToResponseDto(createdComment);
    }

    @Override
    @Transactional
    public ItemResponseDto updateItem(Long userId, Long itemId, ItemUpdateDto itemUpdateDto) {
        log.info("User with id={} updating item with id={}", userId, itemId);

        Item existingItem = itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException("Item with id=" + itemId + " not found")
        );
        Item itemToUpdate = itemMapper.mapToItem(itemUpdateDto);

        if (!existingItem.getOwner().getId().equals(userId)) {
            log.warn("User with id={} trying to update item with id={} owned by another user", userId, itemId);
            throw new NotFoundException("Item with id=" + itemId + " not found");
        }
        if (itemToUpdate.getName() != null && !itemToUpdate.getName().equals(existingItem.getName())) {
            log.debug("Updating item name to {}", itemToUpdate.getName());
            existingItem.setName(itemToUpdate.getName());
        }
        if (itemToUpdate.getDescription() != null && !itemToUpdate.getDescription().equals(existingItem.getDescription())) {
            log.debug("Updating item description to {}", itemToUpdate.getDescription());
            existingItem.setDescription(itemToUpdate.getDescription());
        }
        if (itemToUpdate.getAvailable() != null && !itemToUpdate.getAvailable().equals(existingItem.getAvailable())) {
            log.debug("Updating item availability to {}", itemToUpdate.getAvailable());
            existingItem.setAvailable(itemToUpdate.getAvailable());
        }

        Item createdItem = itemRepository.save(existingItem);
        return itemMapper.mapToResponseDto(createdItem);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemResponseDto> searchItems(Long userId, String searchText) {
        log.info("User with id={} searching for items with query={}", userId, searchText);

        if (searchText == null || searchText.isBlank()) {
            log.warn("Empty search query");
            return Collections.emptyList();
        }

        return itemRepository.searchAvailableItemsByText(searchText)
                .stream()
                .map(itemMapper::mapToResponseDto)
                .toList();
    }

    private void enrichWithBookings(ItemExtendedResponseDto dto, List<Long> itemIds) {
        enrichWithBookings(List.of(dto), itemIds);
    }

    private void enrichWithBookings(List<ItemExtendedResponseDto> dtos, List<Long> itemIds) {
        LocalDateTime now = LocalDateTime.now();

        List<Booking> lastBookings = bookingRepository.findLastBookingsForItems(itemIds, now);
        List<Booking> nextBookings = bookingRepository.findNextBookingsForItems(itemIds, now);

        Map<Long, Booking> lastBookingMap = lastBookings.stream()
                .collect(Collectors.toMap(b -> b.getItem().getId(), b -> b, (b1, b2) -> b1));

        Map<Long, Booking> nextBookingMap = nextBookings.stream()
                .collect(Collectors.toMap(b -> b.getItem().getId(), b -> b, (b1, b2) -> b1));

        dtos.forEach(dto -> {
            if (lastBookingMap.containsKey(dto.getId())) {
                dto.setLastBooking(bookingMapper.mapToShortDto(lastBookingMap.get(dto.getId())));
            }
            if (nextBookingMap.containsKey(dto.getId())) {
                dto.setNextBooking(bookingMapper.mapToShortDto(nextBookingMap.get(dto.getId())));
            }
        });
    }
}