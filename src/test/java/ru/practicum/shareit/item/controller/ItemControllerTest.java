package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemFullPrintDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ItemService itemService;

    private final ItemDto itemDtoWithAnswer = new ItemDto(2L, "Щётка для обуви",
            "Стандартная щётка для обуви", true, 1L);

    private final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("Щётка для машины")
            .description("Стандартная щётка для машины")
            .available(true)
            .build();

    private final CommentDto commentdto = CommentDto.builder()
            .id(1L)
            .text("Some")
            .itemId(1L)
            .authorName("Пупкин")
            .build();

    private final ItemFullPrintDto itemFullPrintDto = ItemFullPrintDto.builder()
            .id(1L)
            .name("Щётка для машины")
            .description("Стандартная щётка для машины")
            .available(true)
            .lastBooking(null)
            .nextBooking(null)
            .comments(List.of())
            .build();


    /**
     * Тесты на проверку метода create
     */

    @Test
    void createItem() throws Exception {
        final Long xSharerUserId = 1L;

        when(itemService.create(anyLong(), any()))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", xSharerUserId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void createItemWithRequestId() throws Exception {
        final Long xSharerUserId = 1L;

        when(itemService.create(anyLong(), any()))
                .thenReturn(itemDtoWithAnswer);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", xSharerUserId)
                        .content(mapper.writeValueAsString(itemDtoWithAnswer))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(itemDtoWithAnswer.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoWithAnswer.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoWithAnswer.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoWithAnswer.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemDtoWithAnswer.getRequestId()), Long.class));
    }


    @Test
    void createComment() throws Exception {
        when(itemService.createComment(anyLong(), anyLong(), any()))
                .thenReturn(commentdto);

        mvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(commentdto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentdto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentdto.getText())))
                .andExpect(jsonPath("$.itemId", is(commentdto.getItemId()), Long.class))
                .andExpect(jsonPath("$.authorName", is(commentdto.getAuthorName())));
    }


    /**
     * Тесты на проверку метода patch
     */


    @Test
    void update() throws Exception {
        final Long xSharerUserId = 1L;

        when(itemService.update(anyLong(), anyLong(), any()))
                .thenReturn(itemDto);

        mvc.perform(patch("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", xSharerUserId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }


    /**
     * Тесты на проверку get
     */

    @Test
    void searchTest() throws Exception {
        final Integer from = 0;
        final Integer size = 10;
        List<ItemDto> dtoItems = List.of(itemDto, itemDtoWithAnswer);
        when(itemService.search("text", from, size))
                .thenReturn(dtoItems);

        mvc.perform(get("/items/search")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .param("text", "text")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(dtoItems))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$[1].id", is(itemDtoWithAnswer.getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(itemDtoWithAnswer.getName())))
                .andExpect(jsonPath("$[1].description", is(itemDtoWithAnswer.getDescription())))
                .andExpect(jsonPath("$[1].available", is(itemDtoWithAnswer.getAvailable())))
                .andExpect(jsonPath("$[1].requestId", is(itemDtoWithAnswer.getRequestId()), Long.class));
    }

    @Test
    void searchTestBlank() throws Exception {
        final Integer from = 0;
        final Integer size = 10;
        final String blank = "  ";
        List<ItemDto> result = List.of();

        when(itemService.search(blank, from, size))
                .thenReturn(result);

        mvc.perform(get("/items/search")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .param("text", blank)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(result))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void searchTestException() throws Exception {
        final Integer from = 0;
        final Integer size = 10;
        String text = null;

        mvc.perform(get("/items/search")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .param("text", text)
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void findByIdTest() throws Exception {
        when(itemService.findById(anyLong(), anyLong()))
                .thenReturn(itemFullPrintDto);

        mvc.perform(get("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemFullPrintDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemFullPrintDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemFullPrintDto.getName())))
                .andExpect(jsonPath("$.description", is(itemFullPrintDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemFullPrintDto.getAvailable())))
                .andExpect(jsonPath("$.lastBooking", is(itemFullPrintDto.getLastBooking())))
                .andExpect(jsonPath("$.nextBooking", is(itemFullPrintDto.getNextBooking())))
                .andExpect(jsonPath("$.comments", hasSize(0)));
    }


    @Test
    void findAllTest() throws Exception {
        final Integer from = 0;
        final Integer size = 10;
        List<ItemFullPrintDto> result = List.of(itemFullPrintDto);
        when(itemService.findAll(1L, from, size))
                .thenReturn(result);

        mvc.perform(get("/items")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(result))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemFullPrintDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemFullPrintDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemFullPrintDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemFullPrintDto.getAvailable())))
                .andExpect(jsonPath("$[0].lastBooking", is(itemFullPrintDto.getLastBooking())))
                .andExpect(jsonPath("$[0].nextBooking", is(itemFullPrintDto.getNextBooking())))
                .andExpect(jsonPath("$[0].comments", hasSize(0)));
    }


    /**
     * Тесты на проверку метода deleteById
     */

    @Test
    void deleteById() throws Exception {
        doNothing().when(itemService).deleteById(1L);

        mvc.perform(delete("/items/{itemId}", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}
