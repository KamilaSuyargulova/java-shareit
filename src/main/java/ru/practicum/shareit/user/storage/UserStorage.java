package ru.practicum.shareit.user.storage;

import jakarta.validation.Valid;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    Collection<User> getAllUsers();

    Optional<User> getUserById(Long userId);

    User addUser(User user);

    User deleteUserById(Long id);

    void checkEmailExists(@Valid User user);
}