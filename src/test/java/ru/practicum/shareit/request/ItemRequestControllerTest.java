package ru.practicum.shareit.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    @Autowired
    ObjectMapper mapper;
    @MockBean
    ItemRequestService itemRequestService;
    @Autowired
    private MockMvc mvc;
    ItemRequestDto itemRequestDto;
    ItemRequestDto itemRequestDtoResponse;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd, HH:mm:ss");
    String created = LocalDateTime.now().plusMinutes(2).format(formatter);

    @BeforeEach
    void setUp() {
        itemRequestDto = ItemRequestDto.builder()
                .description("lalalal")
                .build();

        itemRequestDtoResponse = ItemRequestDto.builder()
                .id(1L)
                .description("lalalal")
                .created(LocalDateTime.parse(created, formatter))
                .build();
    }

    @Test
    void create() throws Exception {
        when(itemRequestService.create(any(), any()))
                .thenReturn(itemRequestDtoResponse);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDtoResponse.getDescription()), String.class))
                .andExpect(jsonPath("$.created", is(String.valueOf(itemRequestDtoResponse.getCreated()))));
    }

    @Test
    void getItemRequestByUserId() throws Exception {
        when(itemRequestService.getAllByUserId(any()))
                .thenReturn(List.of(itemRequestDtoResponse));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequestDtoResponse.getId()), Long.class));
    }

    @Test
    void getAllRequestsByPages() throws Exception {
        when(itemRequestService.findAllByRequestorIdNot(any(), any(), any()))
                .thenReturn(List.of(itemRequestDtoResponse));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 2)
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequestDtoResponse.getId()), Long.class));
    }

    @Test
    void getItemRequestById() throws Exception {
        when(itemRequestService.getByItemRequestId(any(), any()))
                .thenReturn(itemRequestDtoResponse);

        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDtoResponse.getDescription()), String.class))
                .andExpect(jsonPath("$.created", is(String.valueOf(itemRequestDtoResponse.getCreated()))));

    }
}