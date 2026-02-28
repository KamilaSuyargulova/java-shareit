package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private final UserMapper mapper = new UserMapper();

    @Test
    void mapToUser_FromCreateDto_ShouldMapAllFields() {
        UserCreateDto createDto = UserCreateDto.builder()
                .name("John Doe")
                .email("john@example.com")
                .build();

        User result = mapper.mapToUser(createDto);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals("john@example.com", result.getEmail());
    }

    @Test
    void mapToUser_FromUpdateDto_ShouldMapAllFields() {
        UserUpdateDto updateDto = UserUpdateDto.builder()
                .name("Jane Doe")
                .email("jane@example.com")
                .build();

        User result = mapper.mapToUser(updateDto);

        assertNotNull(result);
        assertEquals("Jane Doe", result.getName());
        assertEquals("jane@example.com", result.getEmail());
    }

    @Test
    void mapToUser_FromUpdateDto_WithNullFields_ShouldMapNulls() {
        UserUpdateDto updateDto = UserUpdateDto.builder().build();

        User result = mapper.mapToUser(updateDto);

        assertNotNull(result);
        assertNull(result.getName());
        assertNull(result.getEmail());
    }

    @Test
    void mapToResponseDto_ShouldMapAllFields() {
        User user = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .build();

        UserResponseDto result = mapper.mapToResponseDto(user);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getName());
        assertEquals("john@example.com", result.getEmail());
    }
}