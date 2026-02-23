package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentRequestDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public List<ItemBookingDto> getUsersItemsWithBookings(Long ownerId) {
        checkUserExists(ownerId);
        List<Item> items = itemRepository.findByOwnerIdOrderByIdAsc(ownerId);

        if (items.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> itemIds = items.stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        Map<Long, List<Comment>> commentsByItemId = commentRepository.findByItemIdIn(itemIds)
                .stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));

        LocalDateTime now = LocalDateTime.now();

        Map<Long, BookingDto> lastBookingsMap = getLastBookingsForItems(itemIds, now);
        Map<Long, BookingDto> nextBookingsMap = getNextBookingsForItems(itemIds, now);

        return items.stream()
                .map(item -> {
                    BookingDto lastBooking = lastBookingsMap.get(item.getId());
                    BookingDto nextBooking = nextBookingsMap.get(item.getId());
                    List<Comment> comments = commentsByItemId.getOrDefault(item.getId(), Collections.emptyList());

                    return ItemMapper.toItemBookingDto(item, lastBooking, nextBooking, comments);
                })
                .collect(Collectors.toList());
    }

    @Override
    public ItemBookingDto getItemWithBookingsAndComments(Long itemId, Long userId) {
        Item item = getItemById(itemId);
        List<Comment> comments = commentRepository.findByItemId(itemId);

        BookingDto lastBooking = null;
        BookingDto nextBooking = null;
        LocalDateTime now = LocalDateTime.now();

        if (item.getOwner().getId().equals(userId)) {
            lastBooking = getLastBooking(itemId, now);
            nextBooking = getNextBooking(itemId, now);
        }

        return ItemMapper.toItemBookingDto(item, lastBooking, nextBooking, comments);
    }

    @Override
    public Collection<ItemResponseDto> getAvailableItemsDtoByText(String searchText) {
        if (searchText == null || searchText.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.searchAvailableItems(searchText)
                .stream()
                .map(ItemMapper::toItemResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ItemResponseDto addItem(Long ownerId, ItemRequestDto itemRequestDto) {
        User owner = getUserById(ownerId);
        Item item = ItemMapper.toItem(itemRequestDto);
        item.setOwner(owner);
        return ItemMapper.toItemResponseDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemResponseDto updateItem(Long ownerId, Long itemId, ItemRequestDto itemRequestDto) {
        checkUserExists(ownerId);
        Item itemToUpdate = getItemById(itemId);

        if (!itemToUpdate.getOwner().getId().equals(ownerId)) {
            throw new EntityNotFoundException("У пользователя нет прав для изменения этого предмета");
        }

        if (Objects.nonNull(itemRequestDto.getName()) && !itemRequestDto.getName().isBlank()) {
            itemToUpdate.setName(itemRequestDto.getName());
        }
        if (Objects.nonNull(itemRequestDto.getDescription()) && !itemRequestDto.getDescription().isBlank()) {
            itemToUpdate.setDescription(itemRequestDto.getDescription());
        }
        if (Objects.nonNull(itemRequestDto.getAvailable())) {
            itemToUpdate.setAvailable(itemRequestDto.getAvailable());
        }

        return ItemMapper.toItemResponseDto(itemToUpdate);
    }

    @Override
    @Transactional
    public ItemResponseDto deleteItemById(Long ownerId, Long itemId) {
        checkUserExists(ownerId);
        Item item = getItemById(itemId);

        if (!item.getOwner().getId().equals(ownerId)) {
            throw new EntityNotFoundException("У пользователя нет прав для удаления этого предмета");
        }

        itemRepository.deleteById(itemId);
        return ItemMapper.toItemResponseDto(item);
    }

    @Override
    @Transactional
    public CommentResponseDto addComment(Long userId, Long itemId, CommentRequestDto commentRequestDto) {
        User author = getUserById(userId);
        Item item = getItemById(itemId);

        LocalDateTime now = LocalDateTime.now();
        boolean hasBooked = bookingRepository.existsByBookerIdAndItemIdAndStatusApprovedAndEndBefore(
                userId, itemId, now);

        if (!hasBooked) {
            throw new ValidationException("Пользователь может оставить комментарий только после завершения аренды вещи");
        }

        Comment comment = CommentMapper.toComment(commentRequestDto, item, author);
        return CommentMapper.toCommentResponseDto(commentRepository.save(comment));
    }

    private Map<Long, BookingDto> getLastBookingsForItems(List<Long> itemIds, LocalDateTime now) {
        if (itemIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Booking> lastBookings = bookingRepository.findLastBookingsForItems(itemIds, now);

        return lastBookings.stream()
                .collect(Collectors.toMap(
                        booking -> booking.getItem().getId(),
                        BookingMapper::toBookingDto
                ));
    }

    private Map<Long, BookingDto> getNextBookingsForItems(List<Long> itemIds, LocalDateTime now) {
        if (itemIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Booking> nextBookings = bookingRepository.findNextBookingsForItems(itemIds, now);

        return nextBookings.stream()
                .collect(Collectors.toMap(
                        booking -> booking.getItem().getId(),
                        BookingMapper::toBookingDto
                ));
    }

    private BookingDto getLastBooking(Long itemId, LocalDateTime now) {
        Sort sort = Sort.by(Sort.Direction.DESC, "end");
        List<Booking> lastBookings = bookingRepository.findLastBookingByItemId(itemId, now, sort);

        if (!lastBookings.isEmpty()) {
            return BookingMapper.toBookingDto(lastBookings.get(0));
        }
        return null;
    }

    private BookingDto getNextBooking(Long itemId, LocalDateTime now) {
        Sort sort = Sort.by(Sort.Direction.ASC, "start");
        List<Booking> nextBookings = bookingRepository.findNextBookingByItemId(itemId, now, sort);

        if (!nextBookings.isEmpty()) {
            return BookingMapper.toBookingDto(nextBookings.get(0));
        }
        return null;
    }

    private Item getItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Предмет с id " + id + " не найден"));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с id " + userId + " не найден"));
    }

    private void checkUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("Пользователь с id " + userId + " не найден");
        }
    }
}