package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enumerated.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    void verifyBootstrappingByPersistingUser() {
        User owner = new User();
        owner.setName("Olya");
        owner.setEmail("olya@email.com");

        User booker = new User();
        booker.setName("Pasha");
        booker.setEmail("ldldl@email.com");

        Item item = new Item();
        item.setName("bla");
        item.setDescription("lalal");
        item.setOwner(owner);
        item.setIsAvailable(true);

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusMinutes(2));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        em.persist(owner);
        em.persist(booker);
        em.persist(item);

        Assertions.assertNull(booking.getId());
        em.persist(booking);
        Assertions.assertNotNull(booking.getId());
    }

    @Test
    void verifyRepositoryByPersistingBooking() {
        User owner = new User();
        owner.setName("Olya");
        owner.setEmail("olya@email.com");

        User booker = new User();
        booker.setName("Pasha");
        booker.setEmail("ldldl@email.com");

        Item item = new Item();
        item.setName("bla");
        item.setDescription("lalal");
        item.setOwner(owner);
        item.setIsAvailable(true);

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusMinutes(2));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        userRepository.save(owner);
        userRepository.save(booker);
        itemRepository.save(item);

        Assertions.assertNull(booking.getId());
        bookingRepository.save(booking);
        Assertions.assertNotNull(booking.getId());
    }
}
