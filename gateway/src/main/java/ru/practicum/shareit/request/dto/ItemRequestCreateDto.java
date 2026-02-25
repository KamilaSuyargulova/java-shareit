package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ItemRequestCreateDto {
    @NotBlank(message = "Description cannot be empty")
    @Size(min = 1, message = "Description cannot be less than one character")
    private String description;
}