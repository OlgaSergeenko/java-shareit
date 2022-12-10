package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.shareit.item.dto.ItemCreateRequestDto;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class GatewayItemControllerTest {

    @Autowired
    ObjectMapper mapper;
    @MockBean
    ItemClient itemClient;
    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("Если нет поля name, то возвращается код 500")
    void testCreateNewItemFailNoName() throws Exception {
        ItemCreateRequestDto item = new ItemCreateRequestDto(
                "", "description", true, null);
        MvcResult response = mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(item))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn();

        String message = Objects.requireNonNull(response.getResolvedException()).getMessage();
        assertTrue(message.contains("name required"));
    }

    @Test
    @DisplayName("Если нет поля description, то возвращается код 500")
    void testCreateNewItemFailNoDescription() throws Exception {
        ItemCreateRequestDto item = new ItemCreateRequestDto(
                "name", null, true, null);
        MvcResult response = mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(item))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn();

        String message = Objects.requireNonNull(response.getResolvedException()).getMessage();
        assertTrue(message.contains("description required"));
    }

    @Test
    @DisplayName("Если нет поля description, то возвращается код 500")
    void testCreateNewItemFailNoAvailable() throws Exception {
        ItemCreateRequestDto item = new ItemCreateRequestDto(
                "name", "descr", null, null);
        MvcResult response = mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(item))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn();

        String message = Objects.requireNonNull(response.getResolvedException()).getMessage();
        assertTrue(message.contains("availability required"));
    }
}
