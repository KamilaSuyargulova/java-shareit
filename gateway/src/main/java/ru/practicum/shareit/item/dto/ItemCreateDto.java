package ru.practicum.shareit.Item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemCreateDto {
    @NotBlank(message = "Name cannot be empty")
    @Size(min = 1, message = "Name cannot be less than one character")
    private String name;

    @NotBlank(message = "Description cannot be empty")
    @Size(min = 1, message = "Description cannot be less than one character")
    private String description;

    @NotNull(message = "Available cannot be null")
    private Boolean available;

    @Positive(message = "Request id cannot be negative")
    private Long requestId;
}