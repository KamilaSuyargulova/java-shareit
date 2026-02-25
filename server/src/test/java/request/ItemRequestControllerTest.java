package request;

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
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestExtendedResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.common.Constants.HEADER_USER_ID;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {

    @Mock
    private ItemRequestService itemRequestService;

    @InjectMocks
    private ItemRequestController controller;

    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();
    private static final Long USER_ID = 1L;
    private static final Long REQUEST_ID = 1L;

    private ItemRequestCreateDto createDto;
    private ItemRequestResponseDto responseDto;
    private ItemRequestExtendedResponseDto extendedResponseDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        mapper.registerModule(new JavaTimeModule());

        createDto = new ItemRequestCreateDto();
        createDto.setDescription("Need a drill");

        responseDto = ItemRequestResponseDto.builder()
                .id(REQUEST_ID)
                .description("Need a drill")
                .created(LocalDateTime.now())
                .build();

        extendedResponseDto = ItemRequestExtendedResponseDto.builder()
                .id(REQUEST_ID)
                .description("Need a drill")
                .created(LocalDateTime.now())
                .items(List.of())
                .build();
    }

    @Test
    void createItemRequest_ShouldCreateRequest() throws Exception {
        when(itemRequestService.createItemRequest(anyLong(), any(ItemRequestCreateDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/requests")
                        .header(HEADER_USER_ID, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(REQUEST_ID))
                .andExpect(jsonPath("$.description").value("Need a drill"));
    }

    @Test
    void getAllItemRequests_ShouldReturnRequests() throws Exception {
        when(itemRequestService.getAllItemRequest(anyLong()))
                .thenReturn(List.of(responseDto));

        mockMvc.perform(get("/requests/all")
                        .header(HEADER_USER_ID, 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(REQUEST_ID));
    }

    @Test
    void getAllRequestorItemRequests_ShouldReturnRequestsWithItems() throws Exception {
        when(itemRequestService.getAllRequestorItemRequests(anyLong()))
                .thenReturn(List.of(extendedResponseDto));

        mockMvc.perform(get("/requests")
                        .header(HEADER_USER_ID, USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(REQUEST_ID))
                .andExpect(jsonPath("$[0].description").value("Need a drill"));
    }

    @Test
    void getItemRequestById_ShouldReturnRequest() throws Exception {
        when(itemRequestService.getRequestById(anyLong(), anyLong()))
                .thenReturn(extendedResponseDto);

        mockMvc.perform(get("/requests/{requestId}", REQUEST_ID)
                        .header(HEADER_USER_ID, 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(REQUEST_ID))
                .andExpect(jsonPath("$.description").value("Need a drill"));
    }
}