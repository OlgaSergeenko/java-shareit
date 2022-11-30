package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import java.util.List;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasProperty;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceImplTest {

    private final EntityManager em;
    private final UserService userService;

    @Test
    void saveNewUser() {
        UserDto userDto = new UserDto(null, "Olga", "Olga@email.com");
        userService.create(userDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail()).getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }


    @Test

    void testUpdateUserNewName() {
        UserDto userDto = new UserDto(null, "Olga", "Olga@email.com");
        userService.create(userDto);
        UserDto userDtoUpdate = new UserDto(1L, "LAlala", null);
        userService.update(userDtoUpdate);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.name = :name", User.class);
        User user = query.setParameter("name", userDtoUpdate.getName()).getSingleResult();

        assertThat(user.getId(), equalTo(1L));
        assertThat(user.getName(), equalTo(userDtoUpdate.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void testUpdateUserNewEmail() {
        UserDto userDto = new UserDto(null, "Olga", "Olga@email.com");
        userService.create(userDto);
        UserDto userDtoUpdate = new UserDto(1L, null, "email@email.ru");
        userService.update(userDtoUpdate);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDtoUpdate.getEmail()).getSingleResult();

        assertThat(user.getId(), equalTo(1L));
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDtoUpdate.getEmail()));
    }

    @Test
    void testFailUpdateUserWrongId() {
        UserDto userDto = new UserDto(null, "Olga", "Olga@email.com");
        userService.create(userDto);
        UserDto userDtoUpdate = new UserDto(99L, null, "email@email.ru");//

        final UserNotFoundException exception = Assertions.assertThrows(
                UserNotFoundException.class,
                () ->  userService.update(userDtoUpdate));

        Assertions.assertEquals("User is not found", exception.getMessage());
    }

    @Test
    void getById() {
        UserDto userDto = new UserDto(null, "Olga", "Olga@email.com");
        UserDto userSaved = userService.create(userDto);

        UserDto userFound = userService.getById(userSaved.getId());

        assertThat(userFound.getId(), equalTo(userSaved.getId()));
        assertThat(userFound.getName(), equalTo(userSaved.getName()));
        assertThat(userFound.getEmail(), equalTo(userSaved.getEmail()));

    }

    @Test
    void getByWrongIdFail() {
        UserDto userDto = new UserDto(null, "Olga", "Olga@email.com");
        userService.create(userDto);

        final UserNotFoundException exception = Assertions.assertThrows(
                UserNotFoundException.class,
                () ->  userService.getById(99L));

        Assertions.assertEquals("User is not found", exception.getMessage());

    }

    @Test
    void getAll() {
        // given
        List<UserDto> sourceUsers = List.of(
                new UserDto(1L, "Olga", "olga@mail.ru"),
                new UserDto(2L, "Pasha", "pasha@mail.ru")
        );

        for (UserDto user : sourceUsers) {
            userService.create(user);
        }

        List<UserDto> targetUsers = userService.getAll();

        assertThat(targetUsers, hasSize(sourceUsers.size()));
        for (UserDto sourceUser : sourceUsers) {
            assertThat(targetUsers, hasItem( allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(sourceUser.getName())),
                    hasProperty("email", equalTo(sourceUser.getEmail()))
            )));
        }
    }

    @Test
    void removeUser() {
        UserDto userDto = new UserDto(null, "Olga", "Olga@email.com");
        userService.create(userDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail()).getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));

        userService.removeUser(user.getId());

        final UserNotFoundException exception = Assertions.assertThrows(
                UserNotFoundException.class,
                () ->  userService.getById(user.getId()));

        Assertions.assertEquals("User is not found", exception.getMessage());
    }

    @Test
    @DisplayName("when user exists")
    void TestFindUserIfExistOrElseThrowNotFound() {
        UserDto userDto = new UserDto(null, "Olga", "Olga@email.com");
        UserDto userSaved = userService.create(userDto);

        UserDto userFound = userService.findUserIfExistOrElseThrowNotFound(userSaved.getId());

        assertThat(userFound.getId(), equalTo(userSaved.getId()));
        assertThat(userFound.getName(), equalTo(userSaved.getName()));
        assertThat(userFound.getEmail(), equalTo(userSaved.getEmail()));
    }

    @Test
    @DisplayName("when user does not exist")
    void FindUserIfExistOrElseThrowNotFound() {
        final UserNotFoundException exception = Assertions.assertThrows(
                UserNotFoundException.class,
                () ->  userService.findUserIfExistOrElseThrowNotFound(99L));

        Assertions.assertEquals("User is not found", exception.getMessage());
    }
}