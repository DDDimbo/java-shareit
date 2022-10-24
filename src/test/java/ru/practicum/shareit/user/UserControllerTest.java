package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
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


@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    private final UserDto userDto = UserDto.builder()
            .id(1L)
            .name("John")
            .email("john.doe@mail.com")
            .build();


    /**
     * Тесты на проверку метода create
     */

    @Test
    void create() throws Exception {
        when(userService.create(any()))
                .thenReturn(userDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }


    /**
     * Тесты на проверку метода update
     */

    @Test
    void update() throws Exception {
        UserDto updateUserDto = UserDto.builder()
                .id(1L)
                .name("Alen")
                .email("alen.doe@mail.com")
                .build();

        when(userService.update(anyLong(), any()))
                .thenReturn(updateUserDto);

        mvc.perform(patch("/users/{userId}", 1L)
                        .content(mapper.writeValueAsString(updateUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updateUserDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(updateUserDto.getName())))
                .andExpect(jsonPath("$.email", is(updateUserDto.getEmail())));
    }

//    @Test
//    void saveNewUserDublicateEmailException() throws Exception {
//        when(userService.update(anyLong(), any()))
//                .thenThrow(AlreadyExistsEmailException.class);
//
//        mvc.perform(patch("/users/{userId}", anyLong())
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isConflict());
//    }
//
//    @Test
//    void saveAlreadyExistsUserException() throws Exception {
//        when(userService.update(anyLong(), any()))
//                .thenThrow(UserNotFoundException.class);
//
//        mvc.perform(patch("/users/{userId}", anyLong())
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNotFound());
//    }


    /**
     * Тесты на проверку метода findById
     */

    @Test
    void findById() throws Exception {
        when(userService.findById(anyLong())).thenReturn(userDto);

        mvc.perform(get("/users/{userId}", 1L)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }


    /**
     * Тесты на проверку метода findByAll
     */

    @Test
    void findAll() throws Exception {
        List<UserDto> dtoUsers = Arrays.asList(
                new UserDto(2L, "name2", "name2@mail.ru"),
                new UserDto(3L, "name3", "name3@mail.ru"));
        when(userService.findAll()).thenReturn(dtoUsers);

        mvc.perform(get("/users")
                        .content(mapper.writeValueAsString(dtoUsers))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(2L), Long.class))
                .andExpect(jsonPath("$[0].name", is("name2")))
                .andExpect(jsonPath("$[0].email", is("name2@mail.ru")))
                .andExpect(jsonPath("$[1].id", is(3L), Long.class))
                .andExpect(jsonPath("$[1].name", is("name3")))
                .andExpect(jsonPath("$[1].email", is("name3@mail.ru")));
    }


    /**
     * Тесты на проверку метода deleteById
     */

    @Test
    void deleteById() throws Exception {
        doNothing().when(userService).deleteById(1L);

        mvc.perform(delete("/users/{userId}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}