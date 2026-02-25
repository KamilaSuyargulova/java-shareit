package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserCreateDto {
    @NotBlank(message = "Name cannot be empty")
    @Size(min = 1, message = "Name cannot be less than one character")
    private String name;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email is incorrect")
    private String email;
}