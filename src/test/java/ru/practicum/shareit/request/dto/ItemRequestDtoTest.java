package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.user.dto.UserShortDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoTest {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd, HH:mm:ss");
    private final String created = LocalDateTime.now().plusMinutes(2).format(formatter);
    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void testItemRequestDto() throws Exception {
        ItemShortDto item = new ItemShortDto(1L, "saw");
        UserShortDto user = new UserShortDto(1L);
        ItemRequestDto itemRequestDto = new ItemRequestDto(
                1L,
                "descr",
                LocalDateTime.parse(created, formatter),
                null);

        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo(String.valueOf(itemRequestDto.getDescription()));
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo(String.valueOf(itemRequestDto.getCreated()));
        assertThat(result).extractingJsonPathNumberValue("$.items").isEqualTo(itemRequestDto.getItems());
    }
}
