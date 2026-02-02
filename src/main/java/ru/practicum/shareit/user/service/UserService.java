package ru.practicum.shareit.user.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    Collection<UserDto> getAllUsersDto();

    UserDto getUserDtoById(@Positive Long userId);

    UserDto addNewUser(@Valid UserDto userDto);

    UserDto updateUser(@Positive Long id, @Valid UserDto userDto);

    UserDto deleteUserById(@Positive Long id);
}