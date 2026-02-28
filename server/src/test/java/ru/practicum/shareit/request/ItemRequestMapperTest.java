package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestExtendedResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestMapperTest {

    private final ItemRequestMapper mapper = new ItemRequestMapper();

    @Test
    void mapToResponseDto_shouldMapAllFields() {
        Long id = 1L;
        String description = "Нужна дрель";
        LocalDateTime created = LocalDateTime.now();

        ItemRequest itemRequest = ItemRequest.builder()
                .id(id)
                .description(description)
                .created(created)
                .build();

        ItemRequestResponseDto result = mapper.mapToResponseDto(itemRequest);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(description, result.getDescription());
        assertEquals(created, result.getCreated());
    }

    @Test
    void mapToResponseDto_withNullFields_shouldMapNullsCorrectly() {
        ItemRequest itemRequest = ItemRequest.builder()
                .id(null)
                .description(null)
                .created(null)
                .build();

        ItemRequestResponseDto result = mapper.mapToResponseDto(itemRequest);

        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getDescription());
        assertNull(result.getCreated());
    }

    @Test
    void mapToExtendedResponseDto_shouldMapAllFields() {
        Long id = 1L;
        String description = "Нужна дрель";
        LocalDateTime created = LocalDateTime.now();

        ItemRequest itemRequest = ItemRequest.builder()
                .id(id)
                .description(description)
                .created(created)
                .build();

        ItemRequestExtendedResponseDto result = mapper.mapToExtendedResponseDto(itemRequest);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(description, result.getDescription());
        assertEquals(created, result.getCreated());
    }

    @Test
    void mapToExtendedResponseDto_withNullFields_shouldMapNullsCorrectly() {
        ItemRequest itemRequest = ItemRequest.builder()
                .id(null)
                .description(null)
                .created(null)
                .build();

        ItemRequestExtendedResponseDto result = mapper.mapToExtendedResponseDto(itemRequest);

        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getDescription());
        assertNull(result.getCreated());
    }

    @Test
    void mapToItemRequest_shouldMapAllFields() {
        String description = "Нужна дрель";

        ItemRequestCreateDto createDto = new ItemRequestCreateDto();
        createDto.setDescription(description);

        ItemRequest result = mapper.mapToItemRequest(createDto);

        assertNotNull(result);
        assertEquals(description, result.getDescription());
        assertNull(result.getId());
        assertNull(result.getCreated());
    }

    @Test
    void mapToItemRequest_withNullDescription_shouldMapNullCorrectly() {
        ItemRequestCreateDto createDto = new ItemRequestCreateDto();
        createDto.setDescription(null);

        ItemRequest result = mapper.mapToItemRequest(createDto);

        assertNotNull(result);
        assertNull(result.getDescription());
    }

    @Test
    void mapToItemRequest_shouldCreateNewInstance() {
        String description = "Нужна дрель";

        ItemRequestCreateDto createDto = new ItemRequestCreateDto();
        createDto.setDescription(description);

        ItemRequest result1 = mapper.mapToItemRequest(createDto);
        ItemRequest result2 = mapper.mapToItemRequest(createDto);

        assertNotNull(result1);
        assertNotNull(result2);
        assertNotSame(result1, result2);
    }
}