package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingPrintDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.enums.State;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookingService bookingService;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    private final User user = new User(1L, "updateName", "updateName@user.com");

    private final User owner = new User(2L, "name", "name@user.com");

    private final Item item = new Item(1L, "Аккумуляторная дрель",
            "Аккумуляторная дрель", true, owner, null);

    private final LocalDateTime start = LocalDateTime.now().plusDays(1);
    private final LocalDateTime end = LocalDateTime.now().plusDays(3);

    private final BookingPrintDto bookingPrintDto = BookingPrintDto.builder()
            .id(1L)
            .start(start)
            .end(end)
            .item(item)
            .booker(user)
            .status(Status.APPROVED)
            .build();

    /**
     * Тесты на проверку метода create
     */

    @Test
    void createBookingTest() throws Exception {
        when(bookingService.create(anyLong(), any()))
                .thenReturn(bookingPrintDto);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingPrintDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(bookingPrintDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingPrintDto.getStart().format(formatter))))
                .andExpect(jsonPath("$.end", is(bookingPrintDto.getEnd().format(formatter))))
                .andExpect(jsonPath("$.status", is(String.valueOf(bookingPrintDto.getStatus()))))
                .andExpect(jsonPath("$.item.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(user.getId()), Long.class));
    }

    @Test
    void approveTest() throws Exception {
        when(bookingService.approve(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingPrintDto);

        mvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", String.valueOf(true))
                        .content(mapper.writeValueAsString(bookingPrintDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingPrintDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingPrintDto.getStart().format(formatter))))
                .andExpect(jsonPath("$.end", is(bookingPrintDto.getEnd().format(formatter))))
                .andExpect(jsonPath("$.status", is(String.valueOf(bookingPrintDto.getStatus()))))
                .andExpect(jsonPath("$.item.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(user.getId()), Long.class));
    }

    @Test
    void findByIdTest() throws Exception {
        when(bookingService.findById(anyLong(), anyLong()))
                .thenReturn(bookingPrintDto);

        mvc.perform(get("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingPrintDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingPrintDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingPrintDto.getStart().format(formatter))))
                .andExpect(jsonPath("$.end", is(bookingPrintDto.getEnd().format(formatter))))
                .andExpect(jsonPath("$.status", is(String.valueOf(bookingPrintDto.getStatus()))))
                .andExpect(jsonPath("$.item.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(user.getId()), Long.class));
    }

    @Test
    void findAllByStateTest() throws Exception {
        final Integer from = 0;
        final Integer size = 10;
        final String state = String.valueOf(State.ALL);
        List<BookingPrintDto> result = List.of(bookingPrintDto);
        when(bookingService.findAllByState(1L, state, from, size))
                .thenReturn(result);

        mvc.perform(get("/bookings")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .param("state", state)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(result))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingPrintDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingPrintDto.getStart().format(formatter))))
                .andExpect(jsonPath("$[0].end", is(bookingPrintDto.getEnd().format(formatter))))
                .andExpect(jsonPath("$[0].status", is(String.valueOf(bookingPrintDto.getStatus()))))
                .andExpect(jsonPath("$[0].item.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.id", is(user.getId()), Long.class));
    }

    @Test
    void findAllByParamForOwnerTest() throws Exception {
        final Integer from = 0;
        final Integer size = 10;
        final String state = String.valueOf(State.ALL);
        List<BookingPrintDto> result = List.of(bookingPrintDto);
        when(bookingService.findAllByStateForOwner(2L, state, from, size))
                .thenReturn(result);

        mvc.perform(get("/bookings/owner")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .param("state", state)
                        .header("X-Sharer-User-Id", 2L)
                        .content(mapper.writeValueAsString(result))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingPrintDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingPrintDto.getStart().format(formatter))))
                .andExpect(jsonPath("$[0].end", is(bookingPrintDto.getEnd().format(formatter))))
                .andExpect(jsonPath("$[0].status", is(String.valueOf(bookingPrintDto.getStatus()))))
                .andExpect(jsonPath("$[0].item.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.id", is(user.getId()), Long.class));
    }
}