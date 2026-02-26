package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemShortResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestExtendedResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@ActiveProfiles("test")
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceIT {
    private final ItemRequestService itemRequestService;
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private User requestor;
    private User anotherUser;
    private ItemRequest request1;
    private ItemRequest request2;
    private ItemRequest request3;
    private Item item1ForRequest1;
    private Item item2ForRequest1;
    private Item itemForRequest2;

    @BeforeEach
    void setUp() {

        requestor = User.builder()
                .name("John Requestor")
                .email("john.requestor@example.com")
                .build();
        requestor = userRepository.save(requestor);

        anotherUser = User.builder()
                .name("Jane Another")
                .email("jane.another@example.com")
                .build();
        anotherUser = userRepository.save(anotherUser);

        request1 = ItemRequest.builder()
                .description("Need a laptop for programming")
                .requestor(requestor)
                .created(LocalDateTime.now().minusDays(5))
                .build();
        request1 = itemRequestRepository.save(request1);

        request2 = ItemRequest.builder()
                .description("Looking for a mechanical keyboard")
                .requestor(requestor)
                .created(LocalDateTime.now().minusDays(3))
                .build();
        request2 = itemRequestRepository.save(request2);

        request3 = ItemRequest.builder()
                .description("Need a monitor")
                .requestor(requestor)
                .created(LocalDateTime.now().minusDays(1))
                .build();
        request3 = itemRequestRepository.save(request3);

        ItemRequest otherUserRequest = ItemRequest.builder()
                .description("Request from another user")
                .requestor(anotherUser)
                .created(LocalDateTime.now())
                .build();
        itemRequestRepository.save(otherUserRequest);

        item1ForRequest1 = Item.builder()
                .name("MacBook Pro")
                .description("16-inch, 32GB RAM")
                .available(true)
                .owner(anotherUser)
                .request(request1)
                .build();
        item1ForRequest1 = itemRepository.save(item1ForRequest1);

        item2ForRequest1 = Item.builder()
                .name("Dell XPS")
                .description("15-inch, 16GB RAM")
                .available(true)
                .owner(anotherUser)
                .request(request1)
                .build();
        item2ForRequest1 = itemRepository.save(item2ForRequest1);

        itemForRequest2 = Item.builder()
                .name("Logitech MX Keys")
                .description("Wireless keyboard")
                .available(true)
                .owner(anotherUser)
                .request(request2)
                .build();
        itemForRequest2 = itemRepository.save(itemForRequest2);

    }

    @Test
    void getAllRequestorItemRequests_ShouldReturnAllRequestsWithItems_WhenUserHasRequestsAndItems() {
        List<ItemRequestExtendedResponseDto> result = itemRequestService.getAllRequestorItemRequests(requestor.getId());

        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);

        assertThat(result.get(0).getCreated()).isAfter(result.get(1).getCreated());
        assertThat(result.get(1).getCreated()).isAfter(result.get(2).getCreated());

        ItemRequestExtendedResponseDto firstResponse = result.get(0);
        assertThat(firstResponse.getId()).isEqualTo(request3.getId());
        assertThat(firstResponse.getDescription()).isEqualTo("Need a monitor");
        assertThat(firstResponse.getCreated()).isEqualTo(request3.getCreated());
        assertThat(firstResponse.getItems()).isNullOrEmpty();

        ItemRequestExtendedResponseDto secondResponse = result.get(1);
        assertThat(secondResponse.getId()).isEqualTo(request2.getId());
        assertThat(secondResponse.getDescription()).isEqualTo("Looking for a mechanical keyboard");
        assertThat(secondResponse.getCreated()).isEqualTo(request2.getCreated());
        assertThat(secondResponse.getItems()).hasSize(1);

        ItemShortResponseDto itemForRequest2 = secondResponse.getItems().get(0);
        assertThat(itemForRequest2.getId()).isEqualTo(itemForRequest2.getId());
        assertThat(itemForRequest2.getName()).isEqualTo("Logitech MX Keys");
        assertThat(itemForRequest2.getDescription()).isEqualTo("Wireless keyboard");

        ItemRequestExtendedResponseDto thirdResponse = result.get(2);
        assertThat(thirdResponse.getId()).isEqualTo(request1.getId());
        assertThat(thirdResponse.getDescription()).isEqualTo("Need a laptop for programming");
        assertThat(thirdResponse.getCreated()).isEqualTo(request1.getCreated());
        assertThat(thirdResponse.getItems()).hasSize(2);

        List<ItemShortResponseDto> itemsForRequest1 = thirdResponse.getItems();
        assertThat(itemsForRequest1).extracting(ItemShortResponseDto::getName)
                .containsExactlyInAnyOrder("MacBook Pro", "Dell XPS");
    }

    @Test
    void getAllRequestorItemRequests_ShouldReturnEmptyList_WhenUserHasNoRequests() {
        User userWithNoRequests = User.builder()
                .name("No Requests User")
                .email("norequests@example.com")
                .build();
        userWithNoRequests = userRepository.save(userWithNoRequests);

        List<ItemRequestExtendedResponseDto> result =
                itemRequestService.getAllRequestorItemRequests(userWithNoRequests.getId());

        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    void getAllRequestorItemRequests_ShouldReturnRequestsWithEmptyItems_WhenRequestsHaveNoItems() {
        User userWithEmptyItems = User.builder()
                .name("Empty Requests User")
                .email("emptyrequests@example.com")
                .build();
        userWithEmptyItems = userRepository.save(userWithEmptyItems);

        ItemRequest emptyRequest1 = ItemRequest.builder()
                .description("Request with no items 1")
                .requestor(userWithEmptyItems)
                .created(LocalDateTime.now().minusDays(2))
                .build();
        itemRequestRepository.save(emptyRequest1);

        ItemRequest emptyRequest2 = ItemRequest.builder()
                .description("Request with no items 2")
                .requestor(userWithEmptyItems)
                .created(LocalDateTime.now().minusDays(1))
                .build();
        itemRequestRepository.save(emptyRequest2);

        List<ItemRequestExtendedResponseDto> result =
                itemRequestService.getAllRequestorItemRequests(userWithEmptyItems.getId());

        assertThat(result).hasSize(2);

        result.forEach(response -> {
            assertThat(response.getItems()).isNullOrEmpty();
        });
    }

    @Test
    void getAllRequestorItemRequests_ShouldThrowNotFoundException_WhenUserDoesNotExist() {
        Long nonExistentUserId = 999L;

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.getAllRequestorItemRequests(nonExistentUserId));

        assertThat(exception.getMessage()).contains("User with id=" + nonExistentUserId + " not found");
    }

    @Test
    void getAllRequestorItemRequests_ShouldNotIncludeOtherUsersRequests_WhenUserHasRequests() {
        ItemRequest otherUserRequest1 = ItemRequest.builder()
                .description("Other user request 1")
                .requestor(anotherUser)
                .created(LocalDateTime.now().minusDays(1))
                .build();
        itemRequestRepository.save(otherUserRequest1);

        List<ItemRequestExtendedResponseDto> result =
                itemRequestService.getAllRequestorItemRequests(requestor.getId());

        assertThat(result).hasSize(3);

        result.forEach(response -> {
            assertThat(response.getDescription()).doesNotContain("Other user");
        });
    }

    @Test
    void getAllRequestorItemRequests_ShouldHandleMultipleItemsForSameRequest() {
        Item additionalItem = Item.builder()
                .name("HP Spectre")
                .description("Convertible laptop")
                .available(true)
                .owner(anotherUser)
                .request(request1)
                .build();
        itemRepository.save(additionalItem);

        List<ItemRequestExtendedResponseDto> result =
                itemRequestService.getAllRequestorItemRequests(requestor.getId());

        assertThat(result).hasSize(3);

        ItemRequestExtendedResponseDto request1Response = result.stream()
                .filter(r -> r.getId().equals(request1.getId()))
                .findFirst()
                .orElseThrow();

        assertThat(request1Response.getItems()).hasSize(3);

        List<String> itemNames = request1Response.getItems().stream()
                .map(ItemShortResponseDto::getName)
                .toList();

        assertThat(itemNames).containsExactlyInAnyOrder("MacBook Pro", "Dell XPS", "HP Spectre");
    }

    @Test
    void getAllRequestorItemRequests_ShouldReturnCorrectlyMappedItems() {
        List<ItemRequestExtendedResponseDto> result =
                itemRequestService.getAllRequestorItemRequests(requestor.getId());

        ItemRequestExtendedResponseDto requestWithItems = result.stream()
                .filter(r -> !r.getItems().isEmpty())
                .findFirst()
                .orElseThrow();

        requestWithItems.getItems().forEach(itemDto -> {
            assertThat(itemDto.getId()).isNotNull();
            assertThat(itemDto.getName()).isNotBlank();
            assertThat(itemDto.getDescription()).isNotBlank();

            Item originalItem;
            if (itemDto.getId().equals(item1ForRequest1.getId())) {
                originalItem = item1ForRequest1;
            } else if (itemDto.getId().equals(item2ForRequest1.getId())) {
                originalItem = item2ForRequest1;
            } else {
                originalItem = itemForRequest2;
            }

            assertThat(itemDto.getName()).isEqualTo(originalItem.getName());
            assertThat(itemDto.getDescription()).isEqualTo(originalItem.getDescription());
        });
    }

    @Test
    void getAllRequestorItemRequests_ShouldReturnAllRequestsEvenWithoutItems() {
        ItemRequest requestWithoutItems = ItemRequest.builder()
                .description("Request without items")
                .requestor(requestor)
                .created(LocalDateTime.now())
                .build();
        itemRequestRepository.save(requestWithoutItems);

        List<ItemRequestExtendedResponseDto> result =
                itemRequestService.getAllRequestorItemRequests(requestor.getId());

        assertThat(result).hasSize(4);

        ItemRequestExtendedResponseDto emptyRequest = result.stream()
                .filter(r -> r.getDescription().equals("Request without items"))
                .findFirst()
                .orElseThrow();

        assertThat(emptyRequest.getItems()).isNullOrEmpty();
    }

    @Test
    void getAllRequestorItemRequests_ShouldMaintainDataIntegrity_WhenItemsHaveDifferentOwners() {
        User thirdUser = User.builder()
                .name("Third User")
                .email("third@example.com")
                .build();
        thirdUser = userRepository.save(thirdUser);

        Item itemFromThirdUser = Item.builder()
                .name("ASUS ROG")
                .description("Gaming laptop")
                .available(true)
                .owner(thirdUser)
                .request(request1)
                .build();
        itemRepository.save(itemFromThirdUser);

        List<ItemRequestExtendedResponseDto> result =
                itemRequestService.getAllRequestorItemRequests(requestor.getId());

        ItemRequestExtendedResponseDto request1Response = result.stream()
                .filter(r -> r.getId().equals(request1.getId()))
                .findFirst()
                .orElseThrow();

        assertThat(request1Response.getItems()).hasSize(3);

        List<String> itemNames = request1Response.getItems().stream()
                .map(ItemShortResponseDto::getName)
                .toList();

        assertThat(itemNames).contains("MacBook Pro", "Dell XPS", "ASUS ROG");
    }
}