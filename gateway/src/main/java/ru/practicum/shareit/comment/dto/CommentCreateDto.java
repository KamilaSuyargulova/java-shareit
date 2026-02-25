package ru.practicum.shareit.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateDto {
    @NotBlank(message = "Comment text cannot be blank")
    @Size(min = 1, message = "Comment cannot be less than one character")
    private String text;
}