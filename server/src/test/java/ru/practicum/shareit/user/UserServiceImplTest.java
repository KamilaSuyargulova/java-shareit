package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserCreateDto userCreateDto;
    private UserResponseDto userResponseDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john@test.com")
                .build();

        userCreateDto = UserCreateDto.builder()
                .name("John Doe")
                .email("john@test.com")
                .build();

        userResponseDto = UserResponseDto.builder()
                .id(1L)
                .name("John Doe")
                .email("john@test.com")
                .build();
    }

    @Test
    void getUser_ShouldReturnUser_WhenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.mapToResponseDto(user)).thenReturn(userResponseDto);

        UserResponseDto result = userService.getUser(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getName());
        assertEquals("john@test.com", result.getEmail());
    }

    @Test
    void getUser_ShouldThrowNotFoundException_WhenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUser(99L));
    }

    @Test
    void createUser_ShouldCreateUser_WhenDataIsValid() {
        when(userMapper.mapToUser(userCreateDto)).thenReturn(user);
        when(userRepository.existsByEmail("john@test.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.mapToResponseDto(user)).thenReturn(userResponseDto);

        UserResponseDto result = userService.createUser(userCreateDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getName());
        assertEquals("john@test.com", result.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_ShouldThrowConflictException_WhenEmailAlreadyExists() {
        when(userMapper.mapToUser(userCreateDto)).thenReturn(user);
        when(userRepository.existsByEmail("john@test.com")).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.createUser(userCreateDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_ShouldUpdateUser_WhenDataIsValid() {
        UserUpdateDto updateDto = UserUpdateDto.builder()
                .name("Jane Doe")
                .email("jane@test.com")
                .build();

        User userToUpdate = User.builder()
                .name("Jane Doe")
                .email("jane@test.com")
                .build();

        User updatedUser = User.builder()
                .id(1L)
                .name("Jane Doe")
                .email("jane@test.com")
                .build();

        UserResponseDto updatedResponseDto = UserResponseDto.builder()
                .id(1L)
                .name("Jane Doe")
                .email("jane@test.com")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.mapToUser(updateDto)).thenReturn(userToUpdate);
        when(userRepository.existsByEmail("jane@test.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.mapToResponseDto(updatedUser)).thenReturn(updatedResponseDto);

        UserResponseDto result = userService.updateUser(updateDto, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Jane Doe", result.getName());
        assertEquals("jane@test.com", result.getEmail());
    }

    @Test
    void updateUser_ShouldThrowConflictException_WhenEmailAlreadyExists() {
        UserUpdateDto updateDto = UserUpdateDto.builder()
                .email("existing@test.com")
                .build();

        User userToUpdate = User.builder()
                .email("existing@test.com")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.mapToUser(updateDto)).thenReturn(userToUpdate);
        when(userRepository.existsByEmail("existing@test.com")).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.updateUser(updateDto, 1L));
    }

    @Test
    void deleteUser_ShouldDeleteUser_WhenUserExists() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_ShouldThrowNotFoundException_WhenUserNotFound() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> userService.deleteUser(99L));
        verify(userRepository, never()).deleteById(anyLong());
    }
}