package booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingShortResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BookingMapperTest {

    private final BookingMapper mapper = new BookingMapper();

    @Test
    void mapToBooking_ShouldMapAllFields() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        BookingCreateDto createDto = BookingCreateDto.builder()
                .itemId(1L)
                .start(start)
                .end(end)
                .build();

        Booking result = mapper.mapToBooking(createDto);

        assertNotNull(result);
        assertEquals(start, result.getStartBooking());
        assertEquals(end, result.getEndBooking());
    }

    @Test
    void mapToResponseDto_ShouldMapAllFields() {
        User booker = User.builder().id(1L).name("Booker").build();
        Item item = Item.builder().id(1L).name("Item").build();
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        Booking booking = Booking.builder()
                .id(1L)
                .startBooking(start)
                .endBooking(end)
                .status(BookingStatus.APPROVED)
                .booker(booker)
                .item(item)
                .build();

        BookingResponseDto result = mapper.mapToResponseDto(booking);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(start, result.getStart());
        assertEquals(end, result.getEnd());
        assertEquals(BookingStatus.APPROVED, result.getStatus());
        assertEquals(booker, result.getBooker());
        assertEquals(item, result.getItem());
    }

    @Test
    void mapToShortDto_ShouldMapAllFields() {
        User booker = User.builder().id(2L).build();
        Booking booking = Booking.builder()
                .id(1L)
                .booker(booker)
                .build();

        BookingShortResponseDto result = mapper.mapToShortDto(booking);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(2L, result.getBookerId());
    }
}