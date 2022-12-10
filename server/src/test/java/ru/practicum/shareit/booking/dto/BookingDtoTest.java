package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.enumerated.BookingStatus;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.user.dto.UserShortDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoTest {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd, HH:mm:ss");
    private final String start = LocalDateTime.now().plusMinutes(2).format(formatter);
    private final String end = LocalDateTime.now().plusDays(2).format(formatter);
    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testBookingDto() throws Exception {
        ItemShortDto item = new ItemShortDto(1L, "saw");
        UserShortDto user = new UserShortDto(1L);
        BookingDto bookingDto = new BookingDto(
                1L,
                LocalDateTime.parse(start, formatter),
                LocalDateTime.parse(end, formatter),
                1L,
                item,
                user,
                BookingStatus.WAITING);

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(String.valueOf(bookingDto.getStart()));
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(String.valueOf(bookingDto.getEnd()));
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.item.id")
                .isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.name")
                .isEqualTo("saw");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id")
                .isEqualTo(1);
    }
}
