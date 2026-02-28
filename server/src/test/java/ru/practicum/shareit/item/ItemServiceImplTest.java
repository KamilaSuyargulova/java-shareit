package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.mapper.BookingMapper;
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
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BookingMapper bookingMapper;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User owner;
    private User booker;
    private Item item;
    private ItemRequest itemRequest;
    private Comment comment;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .id(1L)
                .name("Owner")
                .email("owner@test.com")
                .build();

        booker = User.builder()
                .id(2L)
                .name("Booker")
                .email("booker@test.com")
                .build();

        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("Need a drill")
                .requestor(booker)
                .created(LocalDateTime.now())
                .build();

        item = Item.builder()
                .id(1L)
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .owner(owner)
                .request(itemRequest)
                .build();

        comment = Comment.builder()
                .id(1L)
                .text("Great item!")
                .item(item)
                .author(booker)
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    void getItem_ShouldReturnItem_WhenUserIsOwner() {
        when(itemRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(item));
        when(itemMapper.mapToExtendedResponseDto(item)).thenReturn(ItemExtendedResponseDto.builder().id(1L).build());

        ItemExtendedResponseDto result = itemService.getItem(1L, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(itemRepository).findByIdWithDetails(1L);
        verify(itemMapper).mapToExtendedResponseDto(item);
    }

    @Test
    void getItem_ShouldThrowNotFoundException_WhenItemNotFound() {
        when(itemRepository.findByIdWithDetails(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItem(1L, 99L));
    }

    @Test
    void createItem_ShouldCreateItem_WhenDataIsValid() {
        ItemCreateDto createDto = ItemCreateDto.builder()
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .requestId(1L)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemMapper.mapToItem(createDto)).thenReturn(item);
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(itemMapper.mapToResponseDto(item)).thenReturn(ItemResponseDto.builder().id(1L).build());

        ItemResponseDto result = itemService.createItem(1L, createDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void createItem_ShouldThrowNotFoundException_WhenUserNotFound() {
        ItemCreateDto createDto = ItemCreateDto.builder()
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .build();

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.createItem(99L, createDto));
    }

    @Test
    void createItem_ShouldThrowNotFoundException_WhenRequestNotFound() {
        ItemCreateDto createDto = ItemCreateDto.builder()
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .requestId(99L)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemMapper.mapToItem(createDto)).thenReturn(item);
        when(itemRequestRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.createItem(1L, createDto));
    }

    @Test
    void createComment_ShouldCreateComment_WhenUserHasCompletedBooking() {
        CommentCreateDto commentCreateDto = new CommentCreateDto("Great item!");

        when(userRepository.existsById(2L)).thenReturn(true);
        when(itemRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBookingBefore(
                anyLong(), anyLong(), any(BookingStatus.class), any(LocalDateTime.class)))
                .thenReturn(true);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(commentMapper.mapToComment(commentCreateDto)).thenReturn(comment);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(commentMapper.mapToResponseDto(comment)).thenReturn(CommentResponseDto.builder().id(1L).text("Great item!").build());

        CommentResponseDto result = itemService.createComment(2L, 1L, commentCreateDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Great item!", result.getText());
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void createComment_ShouldThrowBadRequestException_WhenUserHasNoCompletedBooking() {
        CommentCreateDto commentCreateDto = new CommentCreateDto("Great item!");

        when(userRepository.existsById(2L)).thenReturn(true);
        when(itemRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBookingBefore(
                anyLong(), anyLong(), any(BookingStatus.class), any(LocalDateTime.class)))
                .thenReturn(false);

        assertThrows(BadRequestException.class, () -> itemService.createComment(2L, 1L, commentCreateDto));
    }

    @Test
    void updateItem_ShouldUpdateItem_WhenUserIsOwner() {
        ItemUpdateDto updateDto = ItemUpdateDto.builder()
                .name("Updated Drill")
                .description("Updated description")
                .available(false)
                .build();

        Item itemToUpdate = Item.builder()
                .name("Updated Drill")
                .description("Updated description")
                .available(false)
                .build();

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemMapper.mapToItem(updateDto)).thenReturn(itemToUpdate);
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(itemMapper.mapToResponseDto(item)).thenReturn(ItemResponseDto.builder().id(1L).build());

        ItemResponseDto result = itemService.updateItem(1L, 1L, updateDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Updated Drill", item.getName());
        assertEquals("Updated description", item.getDescription());
        assertFalse(item.getAvailable());
    }

    @Test
    void updateItem_ShouldThrowNotFoundException_WhenUserIsNotOwner() {
        ItemUpdateDto updateDto = ItemUpdateDto.builder()
                .name("Updated Drill")
                .build();

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemMapper.mapToItem(updateDto)).thenReturn(Item.builder().name("Updated Drill").build());

        assertThrows(NotFoundException.class, () -> itemService.updateItem(3L, 1L, updateDto));
    }

    @Test
    void getUserItems_ShouldReturnItems_WhenUserExists() {
        when(itemRepository.findByOwnerIdWithDetails(1L)).thenReturn(List.of(item));
        when(itemMapper.mapToExtendedResponseDto(item)).thenReturn(ItemExtendedResponseDto.builder().id(1L).build());

        List<ItemExtendedResponseDto> result = itemService.getUserItems(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void searchItems_ShouldReturnItems_WhenSearchTextIsValid() {
        when(itemRepository.searchAvailableItemsByText("drill")).thenReturn(List.of(item));
        when(itemMapper.mapToResponseDto(item)).thenReturn(ItemResponseDto.builder().id(1L).build());

        List<ItemResponseDto> result = itemService.searchItems(1L, "drill");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void searchItems_ShouldReturnEmptyList_WhenSearchTextIsBlank() {
        List<ItemResponseDto> result = itemService.searchItems(1L, "   ");

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(itemRepository, never()).searchAvailableItemsByText(anyString());
    }
}