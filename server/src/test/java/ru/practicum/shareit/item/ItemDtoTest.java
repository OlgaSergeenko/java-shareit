package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void testItemDto() throws Exception {
        UserDto owner = new UserDto(1L, "name", "email@email.ru");
        ItemDto itemDto = new ItemDto(
                1L,
                "name",
                "description",
                true,
                owner,
                null
        );

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(itemDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemDto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(itemDto.getAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.owner.id")
                .isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.owner.name")
                .isEqualTo(owner.getName());
        assertThat(result).extractingJsonPathNumberValue("$.requestId")
                .isEqualTo(itemDto.getRequestId());
    }
}
