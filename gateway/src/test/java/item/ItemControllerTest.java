package item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.shareit.Item.dto.ItemCreateDto;
import ru.practicum.shareit.Item.dto.ItemUpdateDto;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.comment.dto.CommentCreateDto;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ShareItGateway.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper;
    private final WebApplicationContext context;

    @MockBean
    private ru.practicum.shareit.Item.ItemClient itemClient;

    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @SneakyThrows
    void getItem_whenItemIdAndHeaderIsValid_thenReturnOk() {
        when(itemClient.getItem(1L, 1L))
                .thenReturn(ResponseEntity.ok(Map.of(
                        "id", 1L,
                        "name", "Drill",
                        "description", "Power drill",
                        "available", true
                )));

        mockMvc.perform(get("/items/{itemId}", 1L)
                        .header(HEADER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Drill"))
                .andExpect(jsonPath("$.description").value("Power drill"))
                .andExpect(jsonPath("$.available").value(true));

        verify(itemClient).getItem(1L, 1L);
    }

    @Test
    @SneakyThrows
    void getItem_whenItemIdOrHeaderIsInvalid_thenReturnBadRequest() {
        Assertions.assertAll(
                () -> mockMvc.perform(get("/items/{itemId}", 1L)
                                .header(HEADER_USER_ID, -1L))
                        .andExpect(status().isBadRequest()),
                () -> mockMvc.perform(get("/items/{itemId}", 1L)
                                .header(HEADER_USER_ID, 0L))
                        .andExpect(status().isBadRequest()),
                () -> mockMvc.perform(get("/items/{itemId}", -1L)
                                .header(HEADER_USER_ID, 1L))
                        .andExpect(status().isBadRequest()),
                () -> mockMvc.perform(get("/items/{itemId}", 0L)
                                .header(HEADER_USER_ID, 1L))
                        .andExpect(status().isBadRequest())
        );

        verifyNoInteractions(itemClient);
    }

    @Test
    @SneakyThrows
    void getUserItems_whenUserIdIsValid_thenReturnOk() {
        when(itemClient.getUserItems(1L))
                .thenReturn(ResponseEntity.ok(List.of(
                        Map.of(
                                "id", 1L,
                                "name", "Drill",
                                "description", "Power drill",
                                "available", true
                        )
                )));

        mockMvc.perform(get("/items")
                        .header(HEADER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Drill"))
                .andExpect(jsonPath("$[0].description").value("Power drill"))
                .andExpect(jsonPath("$[0].available").value(true));

        verify(itemClient).getUserItems(1L);
    }

    @Test
    @SneakyThrows
    void getUserItems_whenUserIdIsInvalid_thenReturnBadRequest() {
        Assertions.assertAll(
                () -> mockMvc.perform(get("/items")
                                .header(HEADER_USER_ID, -1L))
                        .andExpect(status().isBadRequest()),
                () -> mockMvc.perform(get("/items")
                                .header(HEADER_USER_ID, 0L))
                        .andExpect(status().isBadRequest())
        );

        verifyNoInteractions(itemClient);
    }

    @Test
    @SneakyThrows
    void createItem_whenDtoAndHeaderIsValid_thenReturnOk() {
        ItemCreateDto dto = ItemCreateDto.builder()
                .name("Drill")
                .description("Power drill")
                .available(true)
                .requestId(1L)
                .build();

        when(itemClient.createItem(eq(1L), any(ItemCreateDto.class)))
                .thenReturn(ResponseEntity.ok(Map.of(
                        "id", 1L,
                        "name", dto.getName(),
                        "description", dto.getDescription(),
                        "available", dto.getAvailable()
                )));

        mockMvc.perform(post("/items")
                        .header(HEADER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Drill"))
                .andExpect(jsonPath("$.description").value("Power drill"))
                .andExpect(jsonPath("$.available").value(true));

        verify(itemClient).createItem(eq(1L), any(ItemCreateDto.class));
    }

    @Test
    @SneakyThrows
    void createItem_whenDtoIsNotValid_thenReturnBadRequest() {
        ItemCreateDto nullName = ItemCreateDto.builder()
                .name(null)
                .description("Some description")
                .available(true)
                .build();

        ItemCreateDto blankName = ItemCreateDto.builder()
                .name("")
                .description("Some description")
                .available(true)
                .build();

        ItemCreateDto nullDescription = ItemCreateDto.builder()
                .name("Drill")
                .description(null)
                .available(true)
                .build();

        ItemCreateDto blankDescription = ItemCreateDto.builder()
                .name("Drill")
                .description("")
                .available(true)
                .build();

        ItemCreateDto nullAvailable = ItemCreateDto.builder()
                .name("Drill")
                .description("Power drill")
                .available(null)
                .build();

        ItemCreateDto negativeRequestId = ItemCreateDto.builder()
                .name("Drill")
                .description("Power drill")
                .available(true)
                .requestId(-1L)
                .build();

        Assertions.assertAll(
                () -> mockMvc.perform(post("/items")
                                .header(HEADER_USER_ID, 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(nullName)))
                        .andExpect(status().isBadRequest()),
                () -> mockMvc.perform(post("/items")
                                .header(HEADER_USER_ID, 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(blankName)))
                        .andExpect(status().isBadRequest()),
                () -> mockMvc.perform(post("/items")
                                .header(HEADER_USER_ID, 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(nullDescription)))
                        .andExpect(status().isBadRequest()),
                () -> mockMvc.perform(post("/items")
                                .header(HEADER_USER_ID, 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(blankDescription)))
                        .andExpect(status().isBadRequest()),
                () -> mockMvc.perform(post("/items")
                                .header(HEADER_USER_ID, 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(nullAvailable)))
                        .andExpect(status().isBadRequest()),
                () -> mockMvc.perform(post("/items")
                                .header(HEADER_USER_ID, 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(negativeRequestId)))
                        .andExpect(status().isBadRequest())
        );

        verifyNoInteractions(itemClient);
    }

    @Test
    @SneakyThrows
    void createItem_whenHeaderInvalid_thenReturnBadRequest() {
        ItemCreateDto validDto = ItemCreateDto.builder()
                .name("Drill")
                .description("Power drill")
                .available(true)
                .requestId(1L)
                .build();

        Assertions.assertAll(
                () -> mockMvc.perform(post("/items")
                                .header(HEADER_USER_ID, -1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validDto)))
                        .andExpect(status().isBadRequest()),
                () -> mockMvc.perform(post("/items")
                                .header(HEADER_USER_ID, 0L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validDto)))
                        .andExpect(status().isBadRequest())
        );

        verifyNoInteractions(itemClient);
    }

    @Test
    @SneakyThrows
    void updateItem_whenDtoAndItemIdAndHeaderIsValid_thenReturnOk() {
        ItemUpdateDto validDto = ItemUpdateDto.builder()
                .name("Updated Drill")
                .description("Updated Power drill")
                .available(false)
                .build();

        when(itemClient.updateItem(eq(1L), eq(1L), any(ItemUpdateDto.class)))
                .thenReturn(ResponseEntity.ok(Map.of(
                        "id", 1L,
                        "name", validDto.getName(),
                        "description", validDto.getDescription(),
                        "available", validDto.getAvailable()
                )));

        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .header(HEADER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Updated Drill"))
                .andExpect(jsonPath("$.description").value("Updated Power drill"))
                .andExpect(jsonPath("$.available").value(false));

        verify(itemClient).updateItem(eq(1L), eq(1L), any(ItemUpdateDto.class));
    }

    @Test
    @SneakyThrows
    void updateItem_whenDtoIsNotValid_thenReturnBadRequest() {
        ItemUpdateDto blankName = ItemUpdateDto.builder()
                .name("")
                .description("Some description")
                .available(true)
                .build();

        ItemUpdateDto blankDescription = ItemUpdateDto.builder()
                .name("Drill")
                .description("")
                .available(true)
                .build();

        Assertions.assertAll(
                () -> mockMvc.perform(patch("/items/{itemId}", 1L)
                                .header(HEADER_USER_ID, 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(blankName)))
                        .andExpect(status().isBadRequest()),
                () -> mockMvc.perform(patch("/items/{itemId}", 1L)
                                .header(HEADER_USER_ID, 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(blankDescription)))
                        .andExpect(status().isBadRequest())
        );

        verifyNoInteractions(itemClient);
    }

    @Test
    @SneakyThrows
    void updateItem_whenHeaderOrItemIdInvalid_thenReturnBadRequest() {
        ItemUpdateDto validDto = ItemUpdateDto.builder()
                .name("Updated Drill")
                .description("Updated Power drill")
                .available(true)
                .build();

        Assertions.assertAll(
                () -> mockMvc.perform(patch("/items/{itemId}", 1L)
                                .header(HEADER_USER_ID, -1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validDto)))
                        .andExpect(status().isBadRequest()),
                () -> mockMvc.perform(patch("/items/{itemId}", 1L)
                                .header(HEADER_USER_ID, 0L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validDto)))
                        .andExpect(status().isBadRequest()),
                () -> mockMvc.perform(patch("/items/{itemId}", -1L)
                                .header(HEADER_USER_ID, 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validDto)))
                        .andExpect(status().isBadRequest()),
                () -> mockMvc.perform(patch("/items/{itemId}", 0L)
                                .header(HEADER_USER_ID, 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validDto)))
                        .andExpect(status().isBadRequest())
        );

        verifyNoInteractions(itemClient);
    }

    @Test
    @SneakyThrows
    void searchItems_whenTextValid_thenReturnItems() {
        String searchText = "drill";

        when(itemClient.searchItem(eq(1L), eq(searchText)))
                .thenReturn(ResponseEntity.ok(List.of(
                        Map.of("id", 1L, "name", "Drill"),
                        Map.of("id", 2L, "name", "Hammer Drill")
                )));

        mockMvc.perform(get("/items/search")
                        .header(HEADER_USER_ID, 1L)
                        .param("text", searchText))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Drill"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Hammer Drill"));

        verify(itemClient).searchItem(eq(1L), eq(searchText));
    }

    @Test
    @SneakyThrows
    void searchItems_whenHeaderInvalid_thenReturnBadRequest() {
        Assertions.assertAll(
                () -> mockMvc.perform(get("/items/search")
                                .header(HEADER_USER_ID, -1L)
                                .param("text", "drill"))
                        .andExpect(status().isBadRequest()),
                () -> mockMvc.perform(get("/items/search")
                                .header(HEADER_USER_ID, 0L)
                                .param("text", "drill"))
                        .andExpect(status().isBadRequest())
        );

        verifyNoInteractions(itemClient);
    }

    @Test
    @SneakyThrows
    void createComment_whenDtoAndHeaderValid_thenReturnOk() {
        CommentCreateDto dto = CommentCreateDto.builder()
                .text("Nice item")
                .build();

        when(itemClient.createComment(eq(1L), eq(1L), any(CommentCreateDto.class)))
                .thenReturn(ResponseEntity.ok(Map.of(
                        "id", 1L,
                        "text", dto.getText(),
                        "authorName", "User",
                        "created", "2024-01-01T10:00:00"
                )));

        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .header(HEADER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("Nice item"));

        verify(itemClient).createComment(eq(1L), eq(1L), any(CommentCreateDto.class));
    }

    @Test
    @SneakyThrows
    void createComment_whenDtoNotValid_thenReturnBadRequest() {
        CommentCreateDto blankText = CommentCreateDto.builder()
                .text("")
                .build();

        CommentCreateDto nullText = CommentCreateDto.builder()
                .text(null)
                .build();

        Assertions.assertAll(
                () -> mockMvc.perform(post("/items/{itemId}/comment", 1L)
                                .header(HEADER_USER_ID, 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(blankText)))
                        .andExpect(status().isBadRequest()),
                () -> mockMvc.perform(post("/items/{itemId}/comment", 1L)
                                .header(HEADER_USER_ID, 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(nullText)))
                        .andExpect(status().isBadRequest())
        );

        verifyNoInteractions(itemClient);
    }

    @Test
    @SneakyThrows
    void createComment_whenHeaderOrItemIdInvalid_thenReturnBadRequest() {
        CommentCreateDto dto = CommentCreateDto.builder()
                .text("Nice item")
                .build();

        Assertions.assertAll(
                () -> mockMvc.perform(post("/items/{itemId}/comment", 1L)
                                .header(HEADER_USER_ID, -1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                        .andExpect(status().isBadRequest()),
                () -> mockMvc.perform(post("/items/{itemId}/comment", 1L)
                                .header(HEADER_USER_ID, 0L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                        .andExpect(status().isBadRequest()),
                () -> mockMvc.perform(post("/items/{itemId}/comment", -1L)
                                .header(HEADER_USER_ID, 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                        .andExpect(status().isBadRequest()),
                () -> mockMvc.perform(post("/items/{itemId}/comment", 0L)
                                .header(HEADER_USER_ID, 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                        .andExpect(status().isBadRequest())
        );

        verifyNoInteractions(itemClient);
    }
}