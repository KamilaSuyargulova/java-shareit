package ru.practicum.shareit.request.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;

@Data
@RequiredArgsConstructor
public class ItemRequest {
    private Long id;
    @NotBlank
    private String description;
    @NotNull
    private User requestor;
    @NotBlank
    private LocalDate created;

}