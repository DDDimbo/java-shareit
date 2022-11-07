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
import ru.practicum.shareit.exceptions.*;
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
    void createBookingNotFoundExceptionTest() throws Exception {
        when(bookingService.create(anyLong(), any()))
                .thenThrow(new BookingNotFoundException(
                        "Booking с идентификатором " + bookingPrintDto.getId() + " не найден.")
                );

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingPrintDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.Error",
                        is("Booking с идентификатором " + bookingPrintDto.getId() + " не найден.")));
    }

    @Test
    void createBookingAccessErrorForItemExceptionTest() throws Exception {
        when(bookingService.create(anyLong(), any()))
                .thenThrow(new AccessErrorForItemException(
                        "Вещь с указанным id недоступна для запроса на бронирование.")
                );

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingPrintDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.Error",
                        is("Вещь с указанным id недоступна для запроса на бронирование.")));
    }

    @Test
    void createBookingAccessExceptionTest() throws Exception {
        when(bookingService.create(anyLong(), any()))
                .thenThrow(new BookingAccessException(
                        "Владелец не может создать бронь на свою вещь")
                );

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingPrintDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.Error",
                        is("Владелец не может создать бронь на свою вещь")));
    }

    @Test
    void createUserNotFoundExceptionTest() throws Exception {
        when(bookingService.create(anyLong(), any()))
                .thenThrow(new UserNotFoundException("Пользователя с таким id не существует"));

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingPrintDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.Error", is("Пользователя с таким id не существует")));
    }

    @Test
    void createItemNotFoundExceptionTest() throws Exception {
        when(bookingService.create(anyLong(), any()))
                .thenThrow(new ItemNotFoundException(
                        "Item с идентификатором " + bookingPrintDto.getItem().getId() + " не найден.")
                );

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingPrintDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.Error",
                        is("Item с идентификатором " + bookingPrintDto.getItem().getId() + " не найден.")));
    }

    @Test
    void createDateTimeExceptionTest() throws Exception {
        when(bookingService.create(anyLong(), any()))
                .thenThrow(new DateTimeException(
                        "StartTime не может быть после EndTime или равняться ему")
                );

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingPrintDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.Error",
                        is("StartTime не может быть после EndTime или равняться ему")));
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
    void approveAlreadyExistsStatusExceptionTest() throws Exception {
        when(bookingService.approve(anyLong(), anyLong(), anyBoolean()))
                .thenThrow(new AlreadyExistsStatusException("Статус уже подтвержден"));

        mvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", String.valueOf(true))
                        .content(mapper.writeValueAsString(bookingPrintDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.Error", is("Статус уже подтвержден")));
    }

    @Test
    void findByIdUserAccessExceptionTest() throws Exception {
        when(bookingService.approve(anyLong(), anyLong(), anyBoolean()))
                .thenThrow(new UserAccessException(
                        "Данный пользователь не может получить информацию о заданной вещи.")
                );

        mvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", String.valueOf(true))
                        .content(mapper.writeValueAsString(bookingPrintDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.Error",
                        is("Данный пользователь не может получить информацию о заданной вещи.")));
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
    void findAllByStateUnsupportedStateExceptionTest() throws Exception {
        final Integer from = 0;
        final Integer size = 10;
        final String state = "SOME";
        List<BookingPrintDto> result = List.of(bookingPrintDto);
        when(bookingService.findAllByState(1L, state, from, size))
                .thenThrow(new UnsupportedStateException("Unknown state: " + state));

        mvc.perform(get("/bookings")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .param("state", state)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(result))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Unknown state: " + state)));
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

    @Test
    void findAllByStateForOwnerUnsupportedStateExceptionTest() throws Exception {
        final Integer from = 0;
        final Integer size = 10;
        final String state = "SOME";
        List<BookingPrintDto> result = List.of(bookingPrintDto);
        when(bookingService.findAllByStateForOwner(2L, state, from, size))
                .thenThrow(new UnsupportedStateException("Unknown state: " + state));

        mvc.perform(get("/bookings/owner")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .param("state", state)
                        .header("X-Sharer-User-Id", 2L)
                        .content(mapper.writeValueAsString(result))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Unknown state: " + state)));
    }
}