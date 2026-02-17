package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public Collection<UserDto> getAllUsersDto() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    @Override
    public UserDto getUserDtoById(Long userId) {
        return UserMapper.toUserDto(getUserById(userId));
    }

    @Override
    @Transactional
    public UserDto addNewUser(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new RuntimeException("Пользователь с таким email уже существует");
        }
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserDto updateUser(Long id, UserDto userDto) {
        User userToUpdate = getUserById(id);

        if (userDto.getEmail() != null && !userDto.getEmail().equals(userToUpdate.getEmail())) {
            if (userRepository.existsByEmail(userDto.getEmail())) {
                throw new RuntimeException("Пользователь с таким email уже существует");
            }
            userToUpdate.setEmail(userDto.getEmail());
        }

        if (Objects.nonNull(userDto.getName()) && !userDto.getName().isBlank()) {
            userToUpdate.setName(userDto.getName());
        }

        return UserMapper.toUserDto(userToUpdate);
    }

    @Override
    @Transactional
    public UserDto deleteUserById(Long id) {
        User user = getUserById(id);
        userRepository.deleteById(id);
        return UserMapper.toUserDto(user);
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с id " + userId + " не найден"));
    }
}