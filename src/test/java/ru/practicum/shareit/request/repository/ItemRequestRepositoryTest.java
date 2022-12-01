package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRequestRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository repository;

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    void verifyBootstrappingByPersistingUser() {
        User user = new User();
        user.setName("Olya");
        user.setEmail("olya@email.com");

        ItemRequest request = new ItemRequest();
        request.setDescription("lalala");
        request.setCreated(LocalDateTime.now());
        request.setRequestor(new User(1L, "lala", "lala@mail.ru"));

        em.persist(user);
        Assertions.assertNull(request.getId());
        em.persist(request);
        Assertions.assertNotNull(request.getId());
    }

    @Test
    void verifyRepositoryByPersistingAnEmployee() {
        User user = new User();
        user.setName("Olya");
        user.setEmail("olya@email.com");

        ItemRequest request = new ItemRequest();
        request.setDescription("lalala");
        request.setCreated(LocalDateTime.now());
        request.setRequestor(new User(1L, "lala", "lala@mail.ru"));

        userRepository.save(user);
        Assertions.assertNull(request.getId());
        repository.save(request);
        Assertions.assertNotNull(request.getId());
    }
}
