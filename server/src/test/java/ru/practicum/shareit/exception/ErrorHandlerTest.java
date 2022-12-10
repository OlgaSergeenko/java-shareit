package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ErrorHandler.class)
class ErrorHandlerTest {

    @MockBean
    UserService userService;
    @Autowired
    private MockMvc mvc;

    @Test
    void handleUserNotFoundException() throws Exception {
        when(userService.getById(any()))
                .thenThrow(new UserNotFoundException("User is not found"));
        mvc.perform(get("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void handleForbiddenAccessException() {
    }

    @Test
    void handleItemNotFoundException() {
    }

    @Test
    void handleUnavailableBookingException() {
    }

    @Test
    void handleBookingNotFoundException() {
    }

    @Test
    void handleUnsupportedStatusException() {
    }

    @Test
    void handleRequestNotFoundException() {
    }
}