package ru.practicum.shareit.user.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class UserMapperTest {

    @Test
    void toUserTest() {
        UserDto userDto = new UserDto(1L, "Olya", "Olya@mail.ru");
        User user = UserMapper.toUser(userDto);

        assertEquals(1L, user.getId(), "wrong id");
        assertEquals("Olya", user.getName(), "wrong name");
        assertEquals("Olya@mail.ru", user.getEmail(), "wrong email");
    }

    @Test
    void toDtoTest() {
        User user = new User(1L, "Olya", "Olya@mail.ru");
        UserDto userDto = UserMapper.toDto(user);

        assertEquals(1L, userDto.getId(), "wrong id");
        assertEquals("Olya", userDto.getName(), "wrong name");
        assertEquals("Olya@mail.ru", userDto.getEmail(), "wrong email");
    }
}