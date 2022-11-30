package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@ExtendWith(MockitoExtension.class)
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRepositoryTest {
    private final EntityManager em;

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    ItemDto itemDto;
    UserDto userDto;
    User user;
    Item item;
    ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        user = new User(null, "Olga", "Olga@email.com");
        userDto = new UserDto(1L, "Olga", "Olga@email.com");
        item = new Item(1L, "saw", "big power", true, user, itemRequest);
        itemDto = new ItemDto(1L, "saw", "big power", true, new UserDto(
                1L, "Olga", "Olga@email.com"), 1L);
        itemRequest = new ItemRequest(1L, "text", null, null, null);
    }

    @Test
    void testCreateNewItemInDB() {
        userRepository.save(user);
        Item item1 = new Item(null, "saw", "big power", true, user, null);
        itemRepository.save(item1);

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item item = query.setParameter("name", item1.getName()).getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(item1.getName()));
        assertThat(item.getDescription(), equalTo(item1.getDescription()));
    }
}
