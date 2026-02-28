package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUser(Long userId) {
        log.info("Getting user by id={}", userId);

        User existingUser = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User with id=" + userId + " not found"));

        return userMapper.mapToResponseDto(existingUser);
    }

    @Override
    @Transactional
    public UserResponseDto createUser(UserCreateDto userCreateDto) {
        log.info("Creating user with name={} and email={}", userCreateDto.getName(), userCreateDto.getEmail());

        User userToCreate = userMapper.mapToUser(userCreateDto);
        validateEmailExists(userToCreate.getEmail());

        User createdUser = userRepository.save(userToCreate);
        return userMapper.mapToResponseDto(createdUser);
    }

    @Override
    @Transactional
    public UserResponseDto updateUser(UserUpdateDto userUpdateDto, Long userId) {
        log.info("Updating user by id={}", userId);

        User userToUpdate = userMapper.mapToUser(userUpdateDto);
        User existingUser = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User with id=" + userId + " not found"));

        if (userToUpdate.getName() != null && !userToUpdate.getName().equals(existingUser.getName())) {
            log.debug("Updating user name to {}", userToUpdate.getName());
            existingUser.setName(userToUpdate.getName());
        }
        if (userToUpdate.getEmail() != null && !userToUpdate.getEmail().equals(existingUser.getEmail())) {
            log.debug("Updating user email to {}", userToUpdate.getEmail());
            validateEmailExists(userToUpdate.getEmail());
            existingUser.setEmail(userToUpdate.getEmail());
        }

        User updatedUser = userRepository.save(existingUser);
        return userMapper.mapToResponseDto(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        log.info("Deleting user by id={}", userId);

        validateUserExists(userId);
        userRepository.deleteById(userId);
    }

    private void validateUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            log.warn("User with id={} not found", userId);
            throw new NotFoundException("User with id=" + userId + " not found");
        }
    }

    private void validateEmailExists(String email) {
        if (userRepository.existsByEmail(email)) {
            log.warn("User with email={} already exists", email);
            throw new ConflictException("User with email=" + email + " already exists");
        }
    }
}