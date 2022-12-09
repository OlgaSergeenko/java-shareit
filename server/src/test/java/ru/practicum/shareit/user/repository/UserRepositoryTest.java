package ru.practicum.shareit.user.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.user.model.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private UserRepository repository;

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    void verifyBootstrappingByPersistingUser() {
        User user = new User();
        user.setName("Olya");
        user.setEmail("olya@email.com");

        Assertions.assertNull(user.getId());
        em.persist(user);
        Assertions.assertNotNull(user.getId());
    }

    @Test
    void verifyRepositoryByPersistingAnEmployee() {
        User user = new User();
        user.setName("Olya");
        user.setEmail("olya@email.com");

        Assertions.assertNull(user.getId());
        repository.save(user);
        Assertions.assertNotNull(user.getId());
    }
}