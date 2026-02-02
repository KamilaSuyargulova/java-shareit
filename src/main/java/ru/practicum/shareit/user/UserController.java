package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<UserDto> getAllUsers() {
        log.info("Получить всех пользователей");
        return userService.getAllUsersDto();
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getUserById(@Positive @PathVariable(value = "userId") Long userId) {
        log.info("Получить пользователя с id = {}", userId);
        return userService.getUserDtoById(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addNewUser(@Valid @RequestBody UserDto userDto) {
        log.info("Добавить нового пользователя");
        return userService.addNewUser(userDto);
    }

    @PatchMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto updateUser(@Positive @PathVariable(value = "userId") Long userId, @RequestBody UserDto userDto) {
        log.info("Обновить пользователя с id = {}", userId);
        return userService.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto deleteUserById(@Positive @PathVariable(value = "userId") Long userId) {
        log.info("Удалить пользователя с id = {}", userId);
        return userService.deleteUserById(userId);
    }
}