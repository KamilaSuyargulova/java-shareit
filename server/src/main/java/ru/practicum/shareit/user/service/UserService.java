package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

public interface UserService {

    UserResponseDto getUser(Long id);

    UserResponseDto createUser(UserCreateDto userCreateDto);

    UserResponseDto updateUser(UserUpdateDto userUpdateDto, Long id);

    void deleteUser(Long id);
}