package booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ShareItGateway.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper;
    private final WebApplicationContext context;

    @MockBean
    private BookingClient bookingClient;

    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @SneakyThrows
    void getBooking_whenValid_thenReturnOk() {
        when(bookingClient.getBooking(1L, 1L))
                .thenReturn(ResponseEntity.ok(Map.of(
                        "id", 1L,
                        "status", "APPROVED",
                        "start", "2024-01-01T10:00:00",
                        "end", "2024-01-02T10:00:00"
                )));

        mockMvc.perform(get("/bookings/{bookingId}", 1L)
                        .header(HEADER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(bookingClient).getBooking(1L, 1L);
    }

    @Test
    @SneakyThrows
    void getBooking_whenInvalidIds_thenReturnBadRequest() {
        Assertions.assertAll(
                () -> mockMvc.perform(get("/bookings/{bookingId}", 1L)
                                .header(HEADER_USER_ID, -1L))
                        .andExpect(status().isBadRequest()),
                () -> mockMvc.perform(get("/bookings/{bookingId}", 1L)
                                .header(HEADER_USER_ID, 0L))
                        .andExpect(status().isBadRequest()),
                () -> mockMvc.perform(get("/bookings/{bookingId}", -1L)
                                .header(HEADER_USER_ID, 1L))
                        .andExpect(status().isBadRequest()),
                () -> mockMvc.perform(get("/bookings/{bookingId}", 0L)
                                .header(HEADER_USER_ID, 1L))
                        .andExpect(status().isBadRequest())
        );

        verifyNoInteractions(bookingClient);
    }

    @Test
    @SneakyThrows
    void getBookerBookings_whenValid_thenReturnOk() {
        when(bookingClient.getBookerBookings(eq(1L), any(BookingState.class)))
                .thenReturn(ResponseEntity.ok(List.of(
                        Map.of("id", 1L, "status", "APPROVED")
                )));

        mockMvc.perform(get("/bookings")
                        .header(HEADER_USER_ID, 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(bookingClient).getBookerBookings(eq(1L), any(BookingState.class));
    }

    @Test
    @SneakyThrows
    void getBookerBookings_whenInvalidUserId_thenReturnBadRequest() {
        Assertions.assertAll(
                () -> mockMvc.perform(get("/bookings")
                                .header(HEADER_USER_ID, -1L))
                        .andExpect(status().isBadRequest()),
                () -> mockMvc.perform(get("/bookings")
                                .header(HEADER_USER_ID, 0L))
                        .andExpect(status().isBadRequest())
        );

        verifyNoInteractions(bookingClient);
    }

    @Test
    @SneakyThrows
    void getOwnerBookings_whenValid_thenReturnOk() {
        when(bookingClient.getOwnerBookings(eq(1L), any(BookingState.class)))
                .thenReturn(ResponseEntity.ok(List.of(
                        Map.of("id", 1L, "status", "APPROVED")
                )));

        mockMvc.perform(get("/bookings/owner")
                        .header(HEADER_USER_ID, 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(bookingClient).getOwnerBookings(eq(1L), any(BookingState.class));
    }

    @Test
    @SneakyThrows
    void createBooking_whenValid_thenReturnOk() {
        BookingCreateDto dto = BookingCreateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        when(bookingClient.createBooking(eq(1L), any(BookingCreateDto.class)))
                .thenReturn(ResponseEntity.ok(Map.of(
                        "id", 1L,
                        "status", "WAITING"
                )));

        mockMvc.perform(post("/bookings")
                        .header(HEADER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("WAITING"));

        verify(bookingClient).createBooking(eq(1L), any(BookingCreateDto.class));
    }

    @Test
    @SneakyThrows
    void createBooking_whenInvalidData_thenReturnBadRequest() {
        BookingCreateDto invalidDto = BookingCreateDto.builder()
                .itemId(null)
                .start(null)
                .end(null)
                .build();

        mockMvc.perform(post("/bookings")
                        .header(HEADER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }

    @Test
    @SneakyThrows
    void approveBooking_whenValid_thenReturnOk() {
        when(bookingClient.approveBooking(eq(1L), eq(1L), eq(true)))
                .thenReturn(ResponseEntity.ok(Map.of(
                        "id", 1L,
                        "status", "APPROVED"
                )));

        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header(HEADER_USER_ID, 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(bookingClient).approveBooking(eq(1L), eq(1L), eq(true));
    }
}