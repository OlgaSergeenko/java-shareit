package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class GatewayBookingControllerTest {

    @Autowired
    ObjectMapper mapper;
    @MockBean
    BookingClient bookingClient;
    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("Если неверный формат email, то возвращается код 500")
    void testCreateNewBookingWrongStartTime() throws Exception {
        BookItemRequestDto booking = new BookItemRequestDto(1L,
                LocalDateTime.of(2022, 10, 01, 10, 00),
                LocalDateTime.of(2022, 12, 25, 10, 00));
        MvcResult response = mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(booking))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn();

        String message = Objects.requireNonNull(response.getResolvedException()).getMessage();
        assertTrue(message.contains("start time should be in future or present"));
    }

    @Test
    @DisplayName("Если неверный формат email, то возвращается код 500")
    void testCreateNewBookingWrongEndTime() throws Exception {
        BookItemRequestDto booking = new BookItemRequestDto(1L,
                LocalDateTime.now().plusMinutes(5),
                LocalDateTime.now().minusDays(5));
        MvcResult response = mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(booking))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn();

        String message = Objects.requireNonNull(response.getResolvedException()).getMessage();
        assertTrue(message.contains("end time should be in future"));
    }
}
