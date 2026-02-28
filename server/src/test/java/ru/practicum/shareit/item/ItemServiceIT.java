package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemServiceIT {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private User owner;
    private User anotherUser;
    private Item existingItem;

    @BeforeEach
    void setUp() {
        itemRepository.deleteAll();
        userRepository.deleteAll();

        owner = User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();
        owner = userRepository.save(owner);

        anotherUser = User.builder()
                .name("Jane Smith")
                .email("jane.smith@example.com")
                .build();
        anotherUser = userRepository.save(anotherUser);

        existingItem = Item.builder()
                .name("Laptop")
                .description("Gaming laptop")
                .available(true)
                .owner(owner)
                .build();
        existingItem = itemRepository.save(existingItem);
    }

    @Test
    void updateItem_ShouldUpdateAllFields_WhenAllFieldsProvidedAndUserIsOwner() {
        ItemUpdateDto updateDto = ItemUpdateDto.builder()
                .name("MacBook Pro")
                .description("Professional laptop for developers")
                .available(false)
                .build();

        ItemResponseDto result = itemService.updateItem(owner.getId(), existingItem.getId(), updateDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(existingItem.getId());
        assertThat(result.getName()).isEqualTo("MacBook Pro");
        assertThat(result.getDescription()).isEqualTo("Professional laptop for developers");
        assertThat(result.getAvailable()).isFalse();
        assertThat(result.getComments()).isNullOrEmpty();

        Item updatedItem = itemRepository.findById(existingItem.getId()).orElseThrow();
        assertThat(updatedItem.getName()).isEqualTo("MacBook Pro");
        assertThat(updatedItem.getDescription()).isEqualTo("Professional laptop for developers");
        assertThat(updatedItem.getAvailable()).isFalse();
        assertThat(updatedItem.getOwner().getId()).isEqualTo(owner.getId());
    }

    @Test
    void updateItem_ShouldUpdateOnlyName_WhenOnlyNameProvidedAndUserIsOwner() {
        ItemUpdateDto updateDto = ItemUpdateDto.builder()
                .name("MacBook Pro")
                .build();

        ItemResponseDto result = itemService.updateItem(owner.getId(), existingItem.getId(), updateDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(existingItem.getId());
        assertThat(result.getName()).isEqualTo("MacBook Pro");
        assertThat(result.getDescription()).isEqualTo("Gaming laptop");
        assertThat(result.getAvailable()).isTrue();
        assertThat(result.getComments()).isNullOrEmpty();

        Item updatedItem = itemRepository.findById(existingItem.getId()).orElseThrow();
        assertThat(updatedItem.getName()).isEqualTo("MacBook Pro");
        assertThat(updatedItem.getDescription()).isEqualTo("Gaming laptop");
        assertThat(updatedItem.getAvailable()).isTrue();
    }

    @Test
    void updateItem_ShouldUpdateOnlyDescription_WhenOnlyDescriptionProvidedAndUserIsOwner() {
        ItemUpdateDto updateDto = ItemUpdateDto.builder()
                .description("High-end gaming laptop with RTX 3080")
                .build();

        ItemResponseDto result = itemService.updateItem(owner.getId(), existingItem.getId(), updateDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(existingItem.getId());
        assertThat(result.getName()).isEqualTo("Laptop");
        assertThat(result.getDescription()).isEqualTo("High-end gaming laptop with RTX 3080");
        assertThat(result.getAvailable()).isTrue();
        assertThat(result.getComments()).isNullOrEmpty();

        Item updatedItem = itemRepository.findById(existingItem.getId()).orElseThrow();
        assertThat(updatedItem.getName()).isEqualTo("Laptop");
        assertThat(updatedItem.getDescription()).isEqualTo("High-end gaming laptop with RTX 3080");
        assertThat(updatedItem.getAvailable()).isTrue();
    }

    @Test
    void updateItem_ShouldUpdateOnlyAvailability_WhenOnlyAvailabilityProvidedAndUserIsOwner() {
        ItemUpdateDto updateDto = ItemUpdateDto.builder()
                .available(false)
                .build();

        ItemResponseDto result = itemService.updateItem(owner.getId(), existingItem.getId(), updateDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(existingItem.getId());
        assertThat(result.getName()).isEqualTo("Laptop");
        assertThat(result.getDescription()).isEqualTo("Gaming laptop");
        assertThat(result.getAvailable()).isFalse();
        assertThat(result.getComments()).isNullOrEmpty();

        Item updatedItem = itemRepository.findById(existingItem.getId()).orElseThrow();
        assertThat(updatedItem.getName()).isEqualTo("Laptop");
        assertThat(updatedItem.getDescription()).isEqualTo("Gaming laptop");
        assertThat(updatedItem.getAvailable()).isFalse();
    }

    @Test
    void updateItem_ShouldNotUpdate_WhenFieldsAreSameAndUserIsOwner() {
        ItemUpdateDto updateDto = ItemUpdateDto.builder()
                .name("Laptop")
                .description("Gaming laptop")
                .available(true)
                .build();

        ItemResponseDto result = itemService.updateItem(owner.getId(), existingItem.getId(), updateDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(existingItem.getId());
        assertThat(result.getName()).isEqualTo("Laptop");
        assertThat(result.getDescription()).isEqualTo("Gaming laptop");
        assertThat(result.getAvailable()).isTrue();
        assertThat(result.getComments()).isNullOrEmpty();

        Item updatedItem = itemRepository.findById(existingItem.getId()).orElseThrow();
        assertThat(updatedItem.getName()).isEqualTo("Laptop");
        assertThat(updatedItem.getDescription()).isEqualTo("Gaming laptop");
        assertThat(updatedItem.getAvailable()).isTrue();
    }

    @Test
    void updateItem_ShouldThrowNotFoundException_WhenItemDoesNotExist() {
        Long nonExistentItemId = 999L;
        ItemUpdateDto updateDto = ItemUpdateDto.builder()
                .name("New Name")
                .build();

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.updateItem(owner.getId(), nonExistentItemId, updateDto));

        assertThat(exception.getMessage()).contains("Item with id=" + nonExistentItemId + " not found");
    }

    @Test
    void updateItem_ShouldThrowNotFoundException_WhenUserIsNotOwner() {
        ItemUpdateDto updateDto = ItemUpdateDto.builder()
                .name("MacBook Pro")
                .build();

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.updateItem(anotherUser.getId(), existingItem.getId(), updateDto));

        assertThat(exception.getMessage()).contains("Item with id=" + existingItem.getId() + " not found");

        Item unchangedItem = itemRepository.findById(existingItem.getId()).orElseThrow();
        assertThat(unchangedItem.getName()).isEqualTo("Laptop");
        assertThat(unchangedItem.getDescription()).isEqualTo("Gaming laptop");
        assertThat(unchangedItem.getAvailable()).isTrue();
        assertThat(unchangedItem.getOwner().getId()).isEqualTo(owner.getId());
    }

    @Test
    void updateItem_ShouldUpdateWithPartialFields_WhenSomeFieldsNullAndUserIsOwner() {
        ItemUpdateDto updateDto = ItemUpdateDto.builder()
                .name("MacBook Pro")
                .description(null)
                .available(true)
                .build();

        ItemResponseDto result = itemService.updateItem(owner.getId(), existingItem.getId(), updateDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(existingItem.getId());
        assertThat(result.getName()).isEqualTo("MacBook Pro");
        assertThat(result.getDescription()).isEqualTo("Gaming laptop");
        assertThat(result.getAvailable()).isTrue();
        assertThat(result.getComments()).isNullOrEmpty();

        Item updatedItem = itemRepository.findById(existingItem.getId()).orElseThrow();
        assertThat(updatedItem.getName()).isEqualTo("MacBook Pro");
        assertThat(updatedItem.getDescription()).isEqualTo("Gaming laptop");
        assertThat(updatedItem.getAvailable()).isTrue();
    }

    @Test
    void updateItem_ShouldPreserveOwnerAndId_WhenItemIsUpdated() {
        Long originalId = existingItem.getId();
        Long originalOwnerId = owner.getId();

        ItemUpdateDto updateDto = ItemUpdateDto.builder()
                .name("Updated Name")
                .description("Updated Description")
                .available(false)
                .build();

        ItemResponseDto result = itemService.updateItem(owner.getId(), originalId, updateDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(originalId);

        Item updatedItem = itemRepository.findById(originalId).orElseThrow();
        assertThat(updatedItem.getOwner().getId()).isEqualTo(originalOwnerId);
        assertThat(updatedItem.getOwner().getName()).isEqualTo(owner.getName());
        assertThat(updatedItem.getOwner().getEmail()).isEqualTo(owner.getEmail());
    }

    @Test
    void updateItem_ShouldReturnItemResponseDtoWithoutComments_WhenNoCommentsExist() {
        ItemUpdateDto updateDto = ItemUpdateDto.builder()
                .name("Updated Laptop")
                .build();

        ItemResponseDto result = itemService.updateItem(owner.getId(), existingItem.getId(), updateDto);

        assertThat(result).isNotNull();
        assertThat(result.getComments()).isNullOrEmpty();
        assertThat(result.getName()).isEqualTo("Updated Laptop");
        assertThat(result.getDescription()).isEqualTo("Gaming laptop");
        assertThat(result.getAvailable()).isTrue();
    }

    @Test
    void updateItem_ShouldHandleMultipleUpdates_WhenCalledSequentially() {
        ItemUpdateDto firstUpdate = ItemUpdateDto.builder()
                .name("First Update")
                .build();

        ItemUpdateDto secondUpdate = ItemUpdateDto.builder()
                .description("Second Update")
                .build();

        ItemResponseDto firstResult = itemService.updateItem(owner.getId(), existingItem.getId(), firstUpdate);

        assertThat(firstResult.getName()).isEqualTo("First Update");
        assertThat(firstResult.getDescription()).isEqualTo("Gaming laptop");

        ItemResponseDto secondResult = itemService.updateItem(owner.getId(), existingItem.getId(), secondUpdate);

        assertThat(secondResult.getName()).isEqualTo("First Update");
        assertThat(secondResult.getDescription()).isEqualTo("Second Update");

        Item finalItem = itemRepository.findById(existingItem.getId()).orElseThrow();
        assertThat(finalItem.getName()).isEqualTo("First Update");
        assertThat(finalItem.getDescription()).isEqualTo("Second Update");
        assertThat(finalItem.getAvailable()).isTrue();
    }
}