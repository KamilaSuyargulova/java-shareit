package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public Collection<UserDto> getAllUsersDto() {
        return userStorage.getAllUsers()
                .stream()
                .map(UserMapper::jpaToDto)
                .toList();
    }

    @Override
    public UserDto getUserDtoById(Long userId) {
        return UserMapper.jpaToDto(getUserById(userId));
    }

    @Override
    public UserDto addNewUser(UserDto userDto) {
        userStorage.checkEmailExists(UserMapper.dtoToJpa(userDto));
        return UserMapper.jpaToDto(userStorage.addUser(UserMapper.dtoToJpa(userDto)));
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        userStorage.checkEmailExists(UserMapper.dtoToJpa(userDto));
        User userToUpd = getUserById(id);
        if (Objects.nonNull(userDto.getName())
                && !userDto.getName().isBlank()
                && !userToUpd.getName().equals(userDto.getName()))
            userToUpd.setName(userDto.getName());
        if (Objects.nonNull(userDto.getEmail())
                && !userDto.getEmail().isBlank()
                && !userToUpd.getEmail().equals(userDto.getEmail()))
            userToUpd.setEmail(userDto.getEmail());
        return UserMapper.jpaToDto(userToUpd);
    }

    @Override
    public UserDto deleteUserById(Long id) {
        return UserMapper.jpaToDto(userStorage.deleteUserById(id));
    }

    private User getUserById(Long userId) {
        return userStorage.getUserById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
    }
}