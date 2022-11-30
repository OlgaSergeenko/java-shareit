package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.enumerated.BookingState;
import ru.practicum.shareit.enumerated.BookingStatus;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.user.dto.UserShortDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd, HH:mm:ss");
    private final String start = LocalDateTime.now().plusMinutes(2).format(formatter);
    private final String end = LocalDateTime.now().plusDays(2).format(formatter);
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private BookingServiceImpl bookingService;
    @Autowired
    private MockMvc mvc;
    private BookingDto bookingRequestDto;
    private BookingDto bookingResponseDto;

    @BeforeEach
    void makeBooking() {
        bookingRequestDto = BookingDto.builder()
                .start(LocalDateTime.parse(start, formatter))
                .end(LocalDateTime.parse(end, formatter))
                .itemId(1L)
                .build();

        bookingResponseDto = BookingDto.builder()
                .id(1L)
                .start(bookingRequestDto.getStart())
                .end(bookingRequestDto.getEnd())
                .itemId(1L)
                .item(new ItemShortDto(1L, "saw"))
                .booker(new UserShortDto(1L))
                .status(BookingStatus.WAITING)
                .build();
    }

    @Test
    void create() throws Exception {
        when(bookingService.create(any(), any()))
                .thenReturn(bookingResponseDto);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(String.valueOf(bookingResponseDto.getStart()))))
                .andExpect(jsonPath("$.end", is(String.valueOf(bookingResponseDto.getEnd()))))
                .andExpect(jsonPath("$.itemId", is(bookingResponseDto.getItemId()), Long.class))
                .andExpect(jsonPath("$.booker", is(bookingResponseDto.getBooker()), UserShortDto.class))
                .andExpect(jsonPath("$.status", is(bookingResponseDto.getStatus().toString()), String.class));
    }

    @Test
    void setBookingStatus() throws Exception {
        bookingResponseDto.setStatus(BookingStatus.APPROVED);
        when(bookingService.setBookingStatus(any(), any(), anyBoolean()))
                .thenReturn(bookingResponseDto);

        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", String.valueOf(true))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(String.valueOf(bookingResponseDto.getStart()))))
                .andExpect(jsonPath("$.end", is(String.valueOf(bookingResponseDto.getEnd()))))
                .andExpect(jsonPath("$.itemId", is(bookingResponseDto.getItemId()), Long.class))
                .andExpect(jsonPath("$.booker", is(bookingResponseDto.getBooker()), UserShortDto.class))
                .andExpect(jsonPath("$.status", is(String.valueOf(BookingStatus.APPROVED)), String.class));
    }

    @Test
    void getById() throws Exception {
        when(bookingService.getById(any(), any()))
                .thenReturn(bookingResponseDto);

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(String.valueOf(bookingResponseDto.getStart()))))
                .andExpect(jsonPath("$.end", is(String.valueOf(bookingResponseDto.getEnd()))))
                .andExpect(jsonPath("$.itemId", is(bookingResponseDto.getItemId()), Long.class))
                .andExpect(jsonPath("$.booker", is(bookingResponseDto.getBooker()), UserShortDto.class))
                .andExpect(jsonPath("$.status", is(String.valueOf(BookingStatus.WAITING)), String.class));
    }

    @Test
    void findAllByUserId() throws Exception {
        when(bookingService.findAllByUserId(any(), any()))
                .thenReturn(List.of(bookingResponseDto));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingResponseDto.getId()), Long.class));
    }

    @Test
    void findAllByUserIdWithStateParam() throws Exception {
        when(bookingService.findAllByUserId(any(), any()))
                .thenReturn(Collections.emptyList());

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", String.valueOf(BookingState.PAST))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void findAllByUserIdWithPagination() throws Exception {
        when(bookingService.findAllByUserId(any(), any(), any()))
                .thenReturn(List.of(bookingResponseDto));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingResponseDto.getId()), Long.class));
    }

    @Test
    void findAllByOwnerId() throws Exception {
        when(bookingService.findAllByOwnerId(any(), any()))
                .thenReturn(List.of(bookingResponseDto));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingResponseDto.getId()), Long.class));
    }

    @Test
    void findAllByOwnerIdWithStateParam() throws Exception {
        when(bookingService.findAllByOwnerId(any(), any()))
                .thenReturn(Collections.emptyList());

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", String.valueOf(BookingState.PAST))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void findAllByOwnerIdWithPagination() throws Exception {
        when(bookingService.findAllByOwnerId(any(), any(), any()))
                .thenReturn(List.of(bookingResponseDto));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingResponseDto.getId()), Long.class));
    }
}
