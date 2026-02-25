package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserUpdateDto {
    @Size(min = 1, message = "Name cannot be less than one character")
    private String name;

    @Email(message = "Email is incorrect")
    private String email;
}