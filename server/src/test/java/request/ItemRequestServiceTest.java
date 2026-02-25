package request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemRequestMapper itemRequestMapper;
    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemRequestService itemRequestService;

    private User requestor;
    private User otherUser;
    private ItemRequest itemRequest;
    private ItemRequestCreateDto createDto;
    private Item item;

    @BeforeEach
    void setUp() {
        requestor = User.builder()
                .id(1L)
                .name("Requestor")
                .email("requestor@test.com")
                .build();

        otherUser = User.builder()
                .id(2L)
                .name("Other")
                .email("other@test.com")
                .build();

        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("Need a drill")
                .requestor(requestor)
                .created(LocalDateTime.now())
                .build();

        createDto = new ItemRequestCreateDto();
        createDto.setDescription("Need a drill");

        item = Item.builder()
                .id(1L)
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .owner(otherUser)
                .request(itemRequest)
                .build();
    }

    @Test
    void createItemRequest_ShouldCreateRequest_WhenUserExists() {
        when(itemRequestMapper.mapToItemRequest(createDto)).thenReturn(itemRequest);
        when(userRepository.findById(1L)).thenReturn(Optional.of(requestor));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);
        when(itemRequestMapper.mapToResponseDto(itemRequest)).thenReturn(
                ItemRequestResponseDto.builder().id(1L).description("Need a drill").build()
        );

        ItemRequestResponseDto result = itemRequestService.createItemRequest(1L, createDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Need a drill", result.getDescription());
        verify(itemRequestRepository).save(any(ItemRequest.class));
    }

    @Test
    void createItemRequest_ShouldThrowNotFoundException_WhenUserNotFound() {
        when(itemRequestMapper.mapToItemRequest(createDto)).thenReturn(itemRequest);
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.createItemRequest(99L, createDto));
        verify(itemRequestRepository, never()).save(any(ItemRequest.class));
    }

    @Test
    void getAllItemRequest_ShouldReturnRequests_WhenOtherUsersExist() {
        when(itemRequestRepository.findAllByRequestorIdNot(2L)).thenReturn(List.of(itemRequest));
        when(itemRequestMapper.mapToResponseDto(itemRequest)).thenReturn(
                ItemRequestResponseDto.builder().id(1L).description("Need a drill").build()
        );

        List<ItemRequestResponseDto> result = itemRequestService.getAllItemRequest(2L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void getAllRequestorItemRequests_ShouldReturnRequestsWithItems_WhenUserExists() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRequestRepository.findByRequestor_IdOrderByCreatedDesc(1L)).thenReturn(List.of(itemRequest));
        when(itemRepository.findByRequestorIdWithDetails(1L)).thenReturn(List.of(item));
        when(itemMapper.mapToShortResponseDtoForList(anyList())).thenReturn(List.of());

        List<ItemRequestExtendedResponseDto> result = itemRequestService.getAllRequestorItemRequests(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("Need a drill", result.get(0).getDescription());
    }

    @Test
    void getAllRequestorItemRequests_ShouldThrowNotFoundException_WhenUserNotFound() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> itemRequestService.getAllRequestorItemRequests(99L));
    }

    @Test
    void getRequestById_ShouldReturnRequestWithItems_WhenUserExists() {
        when(userRepository.existsById(2L)).thenReturn(true);
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findByRequest_Id(1L)).thenReturn(List.of(item));
        when(itemMapper.mapToShortResponseDtoForList(anyList())).thenReturn(List.of());

        ItemRequestExtendedResponseDto result = itemRequestService.getRequestById(1L, 2L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Need a drill", result.getDescription());
    }

    @Test
    void getRequestById_ShouldThrowNotFoundException_WhenUserNotFound() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> itemRequestService.getRequestById(1L, 99L));
    }

    @Test
    void getRequestById_ShouldThrowNotFoundException_WhenRequestNotFound() {
        when(userRepository.existsById(2L)).thenReturn(true);
        when(itemRequestRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getRequestById(99L, 2L));
    }
}