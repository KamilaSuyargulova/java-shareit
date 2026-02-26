package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

class ItemMapperTest {

    private final ItemMapper mapper = new ItemMapper(new CommentMapper());

    @Test
    void mapToItem_FromCreateDto_ShouldMapAllFields() {
        ItemCreateDto createDto = ItemCreateDto.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .requestId(1L)
                .build();

        Item result = mapper.mapToItem(createDto);

        assertNotNull(result);
        assertEquals("Test Item", result.getName());
        assertEquals("Test Description", result.getDescription());
        assertTrue(result.getAvailable());
    }

    @Test
    void mapToItem_FromUpdateDto_ShouldMapAllFields() {
        ItemUpdateDto updateDto = ItemUpdateDto.builder()
                .name("Updated Item")
                .description("Updated Description")
                .available(false)
                .build();

        Item result = mapper.mapToItem(updateDto);

        assertNotNull(result);
        assertEquals("Updated Item", result.getName());
        assertEquals("Updated Description", result.getDescription());
        assertFalse(result.getAvailable());
    }

    @Test
    void mapToResponseDto_ShouldMapAllFields() {
        User owner = User.builder().id(1L).build();
        Item item = Item.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(owner)
                .build();

        ItemResponseDto result = mapper.mapToResponseDto(item);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Item", result.getName());
        assertEquals("Test Description", result.getDescription());
        assertTrue(result.getAvailable());
    }

    @Test
    void mapToExtendedResponseDto_ShouldMapAllFields() {
        User owner = User.builder().id(1L).build();
        Item item = Item.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(owner)
                .build();

        ItemExtendedResponseDto result = mapper.mapToExtendedResponseDto(item);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Item", result.getName());
        assertEquals("Test Description", result.getDescription());
        assertTrue(result.getAvailable());
    }

    @Test
    void mapToShortResponseDto_ShouldMapAllFields() {
        Item item = Item.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .build();

        ItemShortResponseDto result = mapper.mapToShortResponseDto(item);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Item", result.getName());
        assertEquals("Test Description", result.getDescription());
    }
}