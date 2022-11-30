package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @Autowired
    ObjectMapper mapper;
    @MockBean
    UserService userService;
    UserDto userDto;
    @Autowired
    private MockMvc mvc;

    @BeforeEach
    void makeUser() {
        userDto = new UserDto(1L, "Lala", "lalala@mail.com");
    }

    @Test
    void testCreateNewUser() throws Exception {
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

    @Test
    @DisplayName("Если неверный формат email, то возвращается код 500")
    void testCreateNewUserFailWrongEmail() throws Exception {
        UserDto userDto1 = new UserDto(2L, "Olol", "xxx");
        MvcResult response = mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn();

        String message = Objects.requireNonNull(response.getResolvedException()).getMessage();
        assertTrue(message.contains("email format - xxx@xxx.ru"));
    }

    @Test
    @DisplayName("Если нет поля email, то возвращается код 500")
    void testCreateNewUserFailNoEmail() throws Exception {
        UserDto userDto1 = new UserDto(2L, "Olol", null);
        MvcResult response = mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn();

        String message = Objects.requireNonNull(response.getResolvedException()).getMessage();
        assertTrue(message.contains("email required"));
    }

    @Test
    @DisplayName("Если нет поля name, то возвращается код 500")
    void testCreateNewUserNoName() throws Exception {
        UserDto userDto1 = new UserDto(2L, null, "user@server.com");
        MvcResult response = mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn();

        String message = Objects.requireNonNull(response.getResolvedException()).getMessage();
        assertTrue(message.contains("name required"));
    }

    @Test
    void testUpdateUser() throws Exception {
        userService.create(userDto);
        UserDto userDto1 = new UserDto(1L, null, "user@server.com");
        when(userService.update(any()))
                .thenReturn(new UserDto(1L, "Lala", "user@server.com"));

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.name", is("Lala")))
                .andExpect(jsonPath("$.email", is(userDto1.getEmail())));
    }

    @Test
    void testGetUserById() throws Exception {
        userService.create(userDto);
        when(userService.getById(any()))
                .thenReturn(userDto);

        mvc.perform(get("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void testFindAll() throws Exception {
        userService.create(userDto);
        when(userService.getAll())
                .thenReturn(List.of(userDto));

        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testRemoveUser() throws Exception {
        userService.create(userDto);
        userService.removeUser(userDto.getId());

        mvc.perform(delete("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}