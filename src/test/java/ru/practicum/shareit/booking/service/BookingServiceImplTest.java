package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.enumerated.BookingState;
import ru.practicum.shareit.enumerated.BookingStatus;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.UnavailableBookingException;
import ru.practicum.shareit.exception.UnsupportedStatusException;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserShortDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@Transactional
@ExtendWith(MockitoExtension.class)
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingServiceImplTest {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd, HH:mm:ss");
    private final String start = LocalDateTime.now().plusMinutes(2).format(formatter);
    private final String end = LocalDateTime.now().plusDays(2).format(formatter);
    @InjectMocks
    private BookingServiceImpl bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserServiceImpl userService;
    @Mock
    private ItemService itemService;
    private UserDto userDto;
    private User user;
    private User booker;
    private Item item;
    private ItemRequest itemRequest;
    private BookingDto bookingRequestDto;
    private Booking booking;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Olga", "Olga@email.com");
        booker = new User(2L, "Pasha", "Lalal@email.com");
        userDto = new UserDto(1L, "Olga", "Olga@email.com");
        item = new Item(1L, "saw", "big power", true, user, itemRequest);
        itemRequest = new ItemRequest(1L, "text", null, null, null);
        bookingRequestDto = BookingDto.builder()
                .start(LocalDateTime.parse(start, formatter))
                .end(LocalDateTime.parse(end, formatter))
                .itemId(1L)
                .build();
        booking = Booking.builder()
                .id(1L)
                .start(bookingRequestDto.getStart())
                .end(bookingRequestDto.getEnd())
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
    }

    @Test
    void create() {
        Mockito
                .when(itemService.getItemById(any(Long.class)))
                .thenReturn(item);
        Mockito
                .when(userService.findUserIfExistOrElseThrowNotFound(any(Long.class)))
                .thenReturn(userDto);
        Mockito
                .when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        BookingDto bookingSaved = bookingService.create(bookingRequestDto, 2L);

        assertThat(bookingSaved.getId(), notNullValue());
        assertThat(String.valueOf(bookingSaved.getStart()), equalTo(String.valueOf(bookingRequestDto.getStart())));
        assertThat(String.valueOf(bookingSaved.getEnd()), equalTo(String.valueOf(bookingRequestDto.getEnd())));
        assertThat(bookingSaved.getBooker(), equalTo(new UserShortDto(booker.getId())));
        assertThat(bookingSaved.getItem(), equalTo(new ItemShortDto(item.getId(), item.getName())));
        assertThat(bookingSaved.getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    void createBookingByOwnerFail() {
        Mockito
                .when(itemService.getItemById(any(Long.class)))
                .thenReturn(item);

        final BookingNotFoundException exception = Assertions.assertThrows(
                BookingNotFoundException.class,
                () -> bookingService.create(bookingRequestDto, 1L));

        Assertions.assertEquals("Owner cannot book his own item", exception.getMessage());
    }

    @Test
    void createBookingWhenUnavailable() {
        item.setIsAvailable(false);
        Mockito
                .when(itemService.getItemById(any(Long.class)))
                .thenReturn(item);

        final UnavailableBookingException exception = Assertions.assertThrows(
                UnavailableBookingException.class,
                () -> bookingService.create(bookingRequestDto, 2L));

        Assertions.assertEquals("Booking is not available", exception.getMessage());
    }

    @Test
    void setBookingStatus() {
        Booking bookingApproved = Booking.builder()
                .id(1L)
                .start(bookingRequestDto.getStart())
                .end(bookingRequestDto.getEnd())
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        Mockito
                .when(bookingRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(booking));

        Mockito
                .when(bookingRepository.save(any(Booking.class)))
                .thenReturn(bookingApproved);

        BookingDto bookingWithStatus = bookingService.setBookingStatus(1L, 1L, true);

        assertThat(bookingWithStatus.getId(), notNullValue());
        assertThat(String.valueOf(bookingWithStatus.getStart()), equalTo(String.valueOf(bookingRequestDto.getStart())));
        assertThat(String.valueOf(bookingWithStatus.getEnd()), equalTo(String.valueOf(bookingRequestDto.getEnd())));
        assertThat(bookingWithStatus.getBooker(), equalTo(new UserShortDto(booker.getId())));
        assertThat(bookingWithStatus.getItem(), equalTo(new ItemShortDto(item.getId(), item.getName())));
        assertThat(bookingWithStatus.getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    void setBookingStatusRejected() {
        Booking bookingRejected = Booking.builder()
                .id(1L)
                .start(bookingRequestDto.getStart())
                .end(bookingRequestDto.getEnd())
                .item(item)
                .booker(booker)
                .status(BookingStatus.REJECTED)
                .build();
        Mockito
                .when(bookingRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(booking));

        Mockito
                .when(bookingRepository.save(any(Booking.class)))
                .thenReturn(bookingRejected);

        BookingDto bookingWithStatus = bookingService.setBookingStatus(1L, 1L, false);

        assertThat(bookingWithStatus.getId(), notNullValue());
        assertThat(String.valueOf(bookingWithStatus.getStart()), equalTo(String.valueOf(bookingRequestDto.getStart())));
        assertThat(String.valueOf(bookingWithStatus.getEnd()), equalTo(String.valueOf(bookingRequestDto.getEnd())));
        assertThat(bookingWithStatus.getBooker(), equalTo(new UserShortDto(booker.getId())));
        assertThat(bookingWithStatus.getItem(), equalTo(new ItemShortDto(item.getId(), item.getName())));
        assertThat(bookingWithStatus.getStatus(), equalTo(BookingStatus.REJECTED));
    }

    @Test
    void failSetBookingStatusBookingNotFound() {
        Mockito
                .when(bookingRepository.findById(any(Long.class)))
                .thenReturn(Optional.empty());

        final BookingNotFoundException exception = Assertions.assertThrows(
                BookingNotFoundException.class,
                () -> bookingService.setBookingStatus(1L, 1L, true));

        Assertions.assertEquals("Booking not found 1", exception.getMessage());
    }

    @Test
    void failSetBookingStatusByBooker() {
        Mockito
                .when(bookingRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(booking));

        final BookingNotFoundException exception = Assertions.assertThrows(
                BookingNotFoundException.class,
                () -> bookingService.setBookingStatus(1L, 2L, true));

        Assertions.assertEquals("Booker cannot set status to the booking", exception.getMessage());
    }

    @Test
    void failSetBookingStatusWhenAlreadyApproved() {
        booking.setStatus(BookingStatus.APPROVED);
        Mockito
                .when(bookingRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(booking));

        final UnavailableBookingException exception = Assertions.assertThrows(
                UnavailableBookingException.class,
                () -> bookingService.setBookingStatus(1L, 1L, true));

        Assertions.assertEquals("Status is already set APPROVED", exception.getMessage());
    }

    @Test
    void getById() {
        Mockito
                .when(bookingRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(booking));

        BookingDto bookingFound = bookingService.getById(1L, 1L);

        assertThat(bookingFound.getId(), equalTo(booking.getId()));
        assertThat(String.valueOf(bookingFound.getStart()), equalTo(String.valueOf(booking.getStart())));
        assertThat(String.valueOf(bookingFound.getEnd()), equalTo(String.valueOf(booking.getEnd())));
        assertThat(bookingFound.getBooker(), equalTo(new UserShortDto(booker.getId())));
        assertThat(bookingFound.getItem(), equalTo(new ItemShortDto(item.getId(), item.getName())));
        assertThat(bookingFound.getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    void failGetByIdNotFound() {
        Mockito
                .when(bookingRepository.findById(any(Long.class)))
                .thenReturn(Optional.empty());

        final BookingNotFoundException exception = Assertions.assertThrows(
                BookingNotFoundException.class,
                () -> bookingService.getById(99L, 1L));

        Assertions.assertEquals("Booking with id 99 is not found", exception.getMessage());
    }

    @Test
    void failGetByIdWrongUser() {
        Mockito
                .when(bookingRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(booking));

        final BookingNotFoundException exception = Assertions.assertThrows(
                BookingNotFoundException.class,
                () -> bookingService.getById(1L, 3L));

        Assertions.assertEquals("no rights for this booking", exception.getMessage());
    }

    @Test
    void findAllByUserIdWithStateAll() {
        Mockito
                .when(userService.findUserIfExistOrElseThrowNotFound(any(Long.class)))
                .thenReturn(userDto);
        Mockito
                .when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyLong()))
                .thenReturn(List.of(booking));

        List<BookingDto> bookings = bookingService.findAllByUserId(1L, BookingState.ALL);

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(booking.getId()));
    }

    @Test
    void findAllByUserIdWithStateCurrent() {
        Mockito
                .when(userService.findUserIfExistOrElseThrowNotFound(any(Long.class)))
                .thenReturn(userDto);
        Mockito
                .when(bookingRepository
                        .findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                                anyLong(), any(), any()))
                .thenReturn(List.of(booking));

        List<BookingDto> bookings = bookingService.findAllByUserId(1L, BookingState.CURRENT);

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(booking.getId()));
    }

    @Test
    void findAllByUserIdWithStatePast() {
        Mockito
                .when(userService.findUserIfExistOrElseThrowNotFound(any(Long.class)))
                .thenReturn(userDto);
        Mockito
                .when(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(
                        anyLong(), any()))
                .thenReturn(List.of(booking));

        List<BookingDto> bookings = bookingService.findAllByUserId(1L, BookingState.PAST);

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(booking.getId()));
    }

    @Test
    void findAllByUserIdWithStateFuture() {
        Mockito
                .when(userService.findUserIfExistOrElseThrowNotFound(any(Long.class)))
                .thenReturn(userDto);
        Mockito
                .when(bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(
                        anyLong(), any()))
                .thenReturn(List.of(booking));

        List<BookingDto> bookings = bookingService.findAllByUserId(1L, BookingState.FUTURE);

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(booking.getId()));
    }

    @Test
    void findAllByUserIdWithStateWaiting() {
        Mockito
                .when(userService.findUserIfExistOrElseThrowNotFound(any(Long.class)))
                .thenReturn(userDto);
        Mockito
                .when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                        anyLong(), any()))
                .thenReturn(List.of(booking));

        List<BookingDto> bookings = bookingService.findAllByUserId(1L, BookingState.WAITING);

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(booking.getId()));
    }

    @Test
    void findAllByUserIdWithStateRejected() {
        Mockito
                .when(userService.findUserIfExistOrElseThrowNotFound(any(Long.class)))
                .thenReturn(userDto);
        Mockito
                .when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                        anyLong(), any()))
                .thenReturn(List.of(booking));

        List<BookingDto> bookings = bookingService.findAllByUserId(1L, BookingState.REJECTED);

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(booking.getId()));
    }

    @Test
    void failfindAllByUserIdWithStateUnsupported() {
        Mockito
                .when(userService.findUserIfExistOrElseThrowNotFound(any(Long.class)))
                .thenReturn(userDto);

        final UnsupportedStatusException exception = Assertions.assertThrows(
                UnsupportedStatusException.class,
                () -> bookingService.findAllByUserId(1L, BookingState.UNSUPPORTED_STATUS));

        Assertions.assertEquals("Unknown state: UNSUPPORTED_STATUS", exception.getMessage());
    }

    @Test
    void findAllByOwnerIdWithStateAll() {
        Mockito
                .when(userService.findUserIfExistOrElseThrowNotFound(any(Long.class)))
                .thenReturn(userDto);
        Mockito
                .when(bookingRepository.findAllByItem_OwnerIdOrderByStartDesc(anyLong()))
                .thenReturn(List.of(booking));

        List<BookingDto> bookings = bookingService.findAllByOwnerId(1L, BookingState.ALL);

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(booking.getId()));
    }

    @Test
    void findAllByOwnerIdWithStateCurrent() {
        Mockito
                .when(userService.findUserIfExistOrElseThrowNotFound(any(Long.class)))
                .thenReturn(userDto);
        Mockito
                .when(bookingRepository
                        .findAllByItem_OwnerIdAndStartLessThanEqualAndEndGreaterThanOrderByStart(
                                anyLong(), any(), any()))
                .thenReturn(List.of(booking));

        List<BookingDto> bookings = bookingService.findAllByOwnerId(1L, BookingState.CURRENT);

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(booking.getId()));
    }

    @Test
    void findAllByOwnerIdWithStatePast() {
        Mockito
                .when(userService.findUserIfExistOrElseThrowNotFound(any(Long.class)))
                .thenReturn(userDto);
        Mockito
                .when(bookingRepository.findAllByItem_OwnerIdAndEndLessThanEqual(
                        anyLong(), any()))
                .thenReturn(List.of(booking));

        List<BookingDto> bookings = bookingService.findAllByOwnerId(1L, BookingState.PAST);

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(booking.getId()));
    }

    @Test
    void findAllByOwnerIdWithStateFuture() {
        Mockito
                .when(userService.findUserIfExistOrElseThrowNotFound(any(Long.class)))
                .thenReturn(userDto);
        Mockito
                .when(bookingRepository.findAllByItem_OwnerIdAndStartGreaterThanEqualOrderByStartDesc(
                        anyLong(), any()))
                .thenReturn(List.of(booking));

        List<BookingDto> bookings = bookingService.findAllByOwnerId(1L, BookingState.FUTURE);

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(booking.getId()));
    }

    @Test
    void findAllByOwnerIdWithStateWaiting() {
        Mockito
                .when(userService.findUserIfExistOrElseThrowNotFound(any(Long.class)))
                .thenReturn(userDto);
        Mockito
                .when(bookingRepository.findAllByItem_OwnerIdAndStatusOrderByStart(
                        anyLong(), any()))
                .thenReturn(List.of(booking));

        List<BookingDto> bookings = bookingService.findAllByOwnerId(1L, BookingState.WAITING);

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(booking.getId()));
    }

    @Test
    void findAllByOwnerIdWithStateRejected() {
        Mockito
                .when(userService.findUserIfExistOrElseThrowNotFound(any(Long.class)))
                .thenReturn(userDto);
        Mockito
                .when(bookingRepository.findAllByItem_OwnerIdAndStatus(
                        anyLong(), any()))
                .thenReturn(List.of(booking));

        List<BookingDto> bookings = bookingService.findAllByOwnerId(1L, BookingState.REJECTED);

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(booking.getId()));
    }

    @Test
    void failFindAllByOwnerIdWithStateUnsupported() {
        Mockito
                .when(userService.findUserIfExistOrElseThrowNotFound(any(Long.class)))
                .thenReturn(userDto);

        final UnsupportedStatusException exception = Assertions.assertThrows(
                UnsupportedStatusException.class,
                () -> bookingService.findAllByOwnerId(1L, BookingState.UNSUPPORTED_STATUS));

        Assertions.assertEquals("Unknown state: UNSUPPORTED_STATUS", exception.getMessage());
    }

    @Test
    void testFindAllByUserId() {
        Mockito
                .when(bookingRepository.findAllByBookerIdOrderByStartDesc(
                        anyLong(), any()))
                .thenReturn(List.of(booking));

        List<BookingDto> bookings = bookingService.findAllByUserId(1L, 0, 1);

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(booking.getId()));
    }

    @Test
    void testFindAllByOwnerId() {
        Mockito
                .when(bookingRepository.findAllByItem_OwnerIdOrderByStartDesc(
                        anyLong(), any()))
                .thenReturn(List.of(booking));

        List<BookingDto> bookings = bookingService.findAllByOwnerId(1L, 0, 1);

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(booking.getId()));
    }
}