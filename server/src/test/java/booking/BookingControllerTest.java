package booking;

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
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.common.Constants.HEADER_USER_ID;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingServiceImpl bookingService;

    @InjectMocks
    private BookingController controller;

    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();
    private static final Long USER_ID = 1L;
    private static final Long BOOKING_ID = 1L;
    private static final Long ITEM_ID = 1L;
    private static final Long OWNER_ID = 2L;

    private User booker;
    private User owner;
    private Item item;
    private BookingResponseDto bookingResponseDto;
    private BookingCreateDto bookingCreateDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        mapper.registerModule(new JavaTimeModule());

        booker = new User();
        booker.setId(USER_ID);
        booker.setName("Booker");
        booker.setEmail("booker@example.com");

        owner = new User();
        owner.setId(OWNER_ID);
        owner.setName("Owner");
        owner.setEmail("owner@example.com");

        item = new Item();
        item.setId(ITEM_ID);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(owner);

        bookingCreateDto = BookingCreateDto.builder()
                .itemId(ITEM_ID)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        bookingResponseDto = BookingResponseDto.builder()
                .id(BOOKING_ID)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .booker(booker)
                .item(item)
                .build();
    }

    @Test
    void getBooking_ShouldReturnBooking() throws Exception {
        when(bookingService.getBooking(anyLong(), anyLong())).thenReturn(bookingResponseDto);

        mockMvc.perform(get("/bookings/{bookingId}", BOOKING_ID)
                        .header(HEADER_USER_ID, OWNER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(BOOKING_ID))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void getBookerBookings_ShouldReturnBookings() throws Exception {
        when(bookingService.getBookerBookings(anyLong(), any(BookingState.class)))
                .thenReturn(List.of(bookingResponseDto));

        mockMvc.perform(get("/bookings")
                        .header(HEADER_USER_ID, USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(BOOKING_ID));
    }

    @Test
    void getOwnerBookings_ShouldReturnBookings() throws Exception {
        when(bookingService.getOwnerBookings(anyLong(), any(BookingState.class)))
                .thenReturn(List.of(bookingResponseDto));

        mockMvc.perform(get("/bookings/owner")
                        .header(HEADER_USER_ID, OWNER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(BOOKING_ID));
    }

    @Test
    void createBooking_ShouldCreateBooking() throws Exception {
        when(bookingService.createBooking(anyLong(), any(BookingCreateDto.class)))
                .thenReturn(bookingResponseDto);

        mockMvc.perform(post("/bookings")
                        .header(HEADER_USER_ID, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingCreateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(BOOKING_ID))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void approveBooking_ShouldApproveBooking() throws Exception {
        bookingResponseDto.setStatus(BookingStatus.APPROVED);

        when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingResponseDto);

        mockMvc.perform(patch("/bookings/{bookingId}", BOOKING_ID)
                        .header(HEADER_USER_ID, OWNER_ID)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }
}