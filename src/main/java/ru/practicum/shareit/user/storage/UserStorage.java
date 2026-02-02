package ru.practicum.shareit.user.storage;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    Collection<User> getAllUsers();

    @NotBlank
    Optional<User> getUserById(Long userId);

    User addUser(User user);

    User deleteUserById(Long id);

    void checkEmailExists(@Valid User user);
}