package ru.practicum.shareit.comment;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CommentMapperTest {

    private final CommentMapper mapper = new CommentMapper();

    @Test
    void mapToComment_ShouldMapAllFields() {
        CommentCreateDto createDto = new CommentCreateDto();
        createDto.setText("Great item!");

        Comment result = mapper.mapToComment(createDto);

        assertNotNull(result);
        assertEquals("Great item!", result.getText());
    }

    @Test
    void mapToResponseDto_ShouldMapAllFields() {
        User author = User.builder().id(1L).name("John Doe").build();
        Item item = Item.builder().id(1L).build();
        LocalDateTime created = LocalDateTime.now();

        Comment comment = Comment.builder().id(1L).text("Great item!").author(author).item(item).created(created).build();

        CommentResponseDto result = mapper.mapToResponseDto(comment);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Great item!", result.getText());
        assertEquals("John Doe", result.getAuthorName());
        assertEquals(created, result.getCreated());
    }
}