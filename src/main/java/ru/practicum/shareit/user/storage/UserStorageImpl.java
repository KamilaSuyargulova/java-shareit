package ru.practicum.shareit.user.storage;

import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserStorageImpl implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public @NotBlank Optional<User> getUserById(Long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public User addUser(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User deleteUserById(Long id) {
        return users.remove(id);
    }

    @Override
    public void checkEmailExists(User user) {
        if (users.values().stream()
                .anyMatch(e -> e.getEmail().equalsIgnoreCase(user.getEmail())))
            throw new RuntimeException("Пользователь с таким email уже существует");
    }

    private Long getNextId() {
        long currentId = users.size();
        return ++currentId;
    }
}