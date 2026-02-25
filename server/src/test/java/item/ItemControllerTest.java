package item;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemExtendedResponseDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.common.Constants.HEADER_USER_ID;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController controller;

    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();
    private static final Long USER_ID = 1L;
    private static final Long ITEM_ID = 1L;

    private ItemCreateDto itemCreateDto;
    private ItemResponseDto itemResponseDto;
    private ItemExtendedResponseDto itemExtendedResponseDto;
    private CommentResponseDto commentResponseDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        mapper.registerModule(new JavaTimeModule());

        itemCreateDto = ItemCreateDto.builder()
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .requestId(1L)
                .build();

        itemResponseDto = ItemResponseDto.builder()
                .id(ITEM_ID)
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .build();

        itemExtendedResponseDto = ItemExtendedResponseDto.builder()
                .id(ITEM_ID)
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .build();

        commentResponseDto = CommentResponseDto.builder()
                .id(1L)
                .text("Great item!")
                .authorName("User")
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    void getItem_ShouldReturnItem() throws Exception {
        when(itemService.getItem(anyLong(), anyLong())).thenReturn(itemExtendedResponseDto);

        mockMvc.perform(get("/items/{itemId}", ITEM_ID)
                        .header(HEADER_USER_ID, USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ITEM_ID))
                .andExpect(jsonPath("$.name").value("Drill"))
                .andExpect(jsonPath("$.description").value("Powerful drill"));
    }

    @Test
    void getUserItems_ShouldReturnItemsList() throws Exception {
        when(itemService.getUserItems(anyLong())).thenReturn(List.of(itemExtendedResponseDto));

        mockMvc.perform(get("/items")
                        .header(HEADER_USER_ID, USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(ITEM_ID))
                .andExpect(jsonPath("$[0].name").value("Drill"));
    }

    @Test
    void createItem_ShouldCreateItem() throws Exception {
        when(itemService.createItem(anyLong(), any(ItemCreateDto.class))).thenReturn(itemResponseDto);

        mockMvc.perform(post("/items")
                        .header(HEADER_USER_ID, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemCreateDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(ITEM_ID))
                .andExpect(jsonPath("$.name").value("Drill"));
    }

    @Test
    void createComment_ShouldCreateComment() throws Exception {
        CommentCreateDto commentCreateDto = new CommentCreateDto();
        commentCreateDto.setText("Great item!");

        when(itemService.createComment(anyLong(), anyLong(), any(CommentCreateDto.class)))
                .thenReturn(commentResponseDto);

        mockMvc.perform(post("/items/{itemId}/comment", ITEM_ID)
                        .header(HEADER_USER_ID, 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentCreateDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("Great item!"));
    }

    @Test
    void updateItem_ShouldUpdateItem() throws Exception {
        ItemUpdateDto updateDto = ItemUpdateDto.builder()
                .name("Updated Drill")
                .build();

        ItemResponseDto updatedResponse = ItemResponseDto.builder()
                .id(ITEM_ID)
                .name("Updated Drill")
                .description("Powerful drill")
                .available(true)
                .build();

        when(itemService.updateItem(anyLong(), anyLong(), any(ItemUpdateDto.class)))
                .thenReturn(updatedResponse);

        mockMvc.perform(patch("/items/{itemId}", ITEM_ID)
                        .header(HEADER_USER_ID, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Drill"));
    }

    @Test
    void searchItems_ShouldReturnItems() throws Exception {
        when(itemService.searchItems(anyLong(), anyString())).thenReturn(List.of(itemResponseDto));

        mockMvc.perform(get("/items/search")
                        .header(HEADER_USER_ID, USER_ID)
                        .param("text", "drill"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(ITEM_ID))
                .andExpect(jsonPath("$[0].name").value("Drill"));
    }
}