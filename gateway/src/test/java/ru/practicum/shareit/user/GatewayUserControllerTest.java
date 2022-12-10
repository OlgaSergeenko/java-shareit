package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.shareit.user.dto.UserCreateRequestDto;
import ru.practicum.shareit.user.dto.UserUpdateRequestDto;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class GatewayUserControllerTest {

    @Autowired
    ObjectMapper mapper;
    @MockBean
    UserClient userClient;
    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("Если неверный формат email, то возвращается код 500")
    void testCreateNewUserFailWrongEmail() throws Exception {
        UserCreateRequestDto user = new UserCreateRequestDto("User", "xxx");
        MvcResult response = mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user))
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
        UserCreateRequestDto user = new UserCreateRequestDto("User", null);
        MvcResult response = mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user))
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
        UserCreateRequestDto user = new UserCreateRequestDto(null, "user@server.com");
        MvcResult response = mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn();

        String message = Objects.requireNonNull(response.getResolvedException()).getMessage();
        assertTrue(message.contains("name required"));
    }

    @Test
    @DisplayName("при обновдении почты в неверном формате возвращается код 500")
    void testUpdateUserWrongEmail() throws Exception {
        UserUpdateRequestDto user = new UserUpdateRequestDto(null, "user.com");
        MvcResult response = mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn();

        String message = Objects.requireNonNull(response.getResolvedException()).getMessage();
        assertTrue(message.contains("email format - xxx@xxx.ru"));
    }
}
