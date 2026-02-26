package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceIT {
    private final UserService userService;
    private final UserRepository userRepository;
    private User existingUser;

    @BeforeEach
    void setUp() {
        existingUser = User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        existingUser = userRepository.save(existingUser);
    }

    @Test
    void updateUser_ShouldUpdateAllFields_WhenAllFieldsProvided() {
        UserUpdateDto updateDto = UserUpdateDto.builder()
                .name("Jane Smith")
                .email("jane.smith@example.com")
                .build();

        UserResponseDto result = userService.updateUser(updateDto, existingUser.getId());

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Jane Smith");
        assertThat(result.getEmail()).isEqualTo("jane.smith@example.com");

        User updatedUser = userRepository.findById(existingUser.getId()).orElseThrow();
        assertThat(updatedUser.getName()).isEqualTo("Jane Smith");
        assertThat(updatedUser.getEmail()).isEqualTo("jane.smith@example.com");
    }

    @Test
    void updateUser_ShouldUpdateOnlyName_WhenOnlyNameProvided() {
        UserUpdateDto updateDto = UserUpdateDto.builder()
                .name("Jane Smith")
                .build();

        UserResponseDto result = userService.updateUser(updateDto, existingUser.getId());

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Jane Smith");
        assertThat(result.getEmail()).isEqualTo("john.doe@example.com");

        User updatedUser = userRepository.findById(existingUser.getId()).orElseThrow();
        assertThat(updatedUser.getName()).isEqualTo("Jane Smith");
        assertThat(updatedUser.getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    void updateUser_ShouldUpdateOnlyEmail_WhenOnlyEmailProvided() {
        UserUpdateDto updateDto = UserUpdateDto.builder()
                .email("jane.smith@example.com")
                .build();

        UserResponseDto result = userService.updateUser(updateDto, existingUser.getId());

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("John Doe");
        assertThat(result.getEmail()).isEqualTo("jane.smith@example.com");

        User updatedUser = userRepository.findById(existingUser.getId()).orElseThrow();
        assertThat(updatedUser.getName()).isEqualTo("John Doe");
        assertThat(updatedUser.getEmail()).isEqualTo("jane.smith@example.com");
    }

    @Test
    void updateUser_ShouldNotUpdate_WhenFieldsAreSame() {
        UserUpdateDto updateDto = UserUpdateDto.builder()
                .name("Jane Smith")
                .email("jane.smith@example.com")
                .build();

        UserResponseDto result = userService.updateUser(updateDto, existingUser.getId());

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Jane Smith");
        assertThat(result.getEmail()).isEqualTo("jane.smith@example.com");

        User updatedUser = userRepository.findById(existingUser.getId()).orElseThrow();
        assertThat(updatedUser.getName()).isEqualTo("Jane Smith");
        assertThat(updatedUser.getEmail()).isEqualTo("jane.smith@example.com");
    }

    @Test
    void updateUser_ShouldThrowException_WhenUserNotFound() {
        Long nonExistentUserId = 999L;
        UserUpdateDto updateDto = UserUpdateDto.builder()
                .name("Jane Smith")
                .email("jane.smith@example.com")
                .build();

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.updateUser(updateDto, nonExistentUserId));

        assertThat(exception.getMessage()).contains("User with id=" + nonExistentUserId + " not found");
    }

    @Test
    void updateUser_ShouldThrowException_WhenEmailAlreadyExists() {
        User anotherUser = User.builder()
                .name("Another User")
                .email("another@example.com")
                .build();
        userRepository.save(anotherUser);

        UserUpdateDto updateDto = UserUpdateDto.builder()
                .email("another@example.com")
                .build();

        ConflictException exception = assertThrows(ConflictException.class,
                () -> userService.updateUser(updateDto, existingUser.getId()));

        assertThat(exception.getMessage()).contains("User with email=" + anotherUser.getEmail() + " already exists");

        User unchangedUser = userRepository.findById(existingUser.getId()).orElseThrow();
        assertThat(unchangedUser.getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    void updateUser_ShouldUpdateSuccessfully_WhenOnlyEmailChangedAndEmailIsUnique() {
        UserUpdateDto updateDto = UserUpdateDto.builder()
                .email("new.unique@example.com")
                .build();

        UserResponseDto result = userService.updateUser(updateDto, existingUser.getId());

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(updateDto.getEmail());

        User updatedUser = userRepository.findById(existingUser.getId()).orElseThrow();
        assertThat(updatedUser.getEmail()).isEqualTo(updateDto.getEmail());
    }
}