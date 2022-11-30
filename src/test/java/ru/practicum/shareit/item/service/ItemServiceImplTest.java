package ru.practicum.shareit.item.service;

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
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenAccessException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UnavailableBookingException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.comments.Comment;
import ru.practicum.shareit.item.comments.CommentDto;
import ru.practicum.shareit.item.comments.CommentRepository;
import ru.practicum.shareit.item.dto.ItemBookingCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestRepository.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.*;

@Transactional
@ExtendWith(MockitoExtension.class)
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemServiceImplTest {
    private ItemDto itemDto;
    private ItemDto itemDtoNoRequest;
    private UserDto userDto;
    private User user;
    private User booker;
    private Item item;
    private Item itemNoRequest;
    private ItemRequest itemRequest;
    private Booking booking;
    private Booking last;
    private Booking next;
    private Comment comment;
    private CommentDto commentDto;
    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserService userService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Olga", "Olga@email.com");
        booker = new User(2L, "Papap", "tatat@email.com");
        userDto = new UserDto(1L, "Olga", "Olga@email.com");
        item = new Item(1L, "saw", "big power", true, user, itemRequest);
        itemNoRequest = new Item(1L, "saw", "big power", true, user, null);
        itemDto = new ItemDto(1L, "saw", "big power", true, new UserDto(
                1L, "Olga", "Olga@email.com"), 1L);
        itemDtoNoRequest = new ItemDto(1L, "saw", "big power", true, new UserDto(
                1L, "Olga", "Olga@email.com"), null);
        itemRequest = new ItemRequest(1L, "text", null, null, null);
        booking = Booking.builder()
                .id(1L)
                .build();
        last = Booking.builder()
                .id(2L)
                .booker(booker)
                .build();
        next = Booking.builder()
                .id(3L)
                .booker(booker)
                .build();
        comment = Comment.builder()
                .id(1L)
                .text("bla bla")
                .author(user)
                .item(Item.builder().id(1L).build())
                .creationDate(LocalDateTime.now())
                .build();
        commentDto = CommentDto.builder()
                .text("bla bla")
                .build();
    }

    @Test
    void testCreateNewItem() {
        Mockito.
                when(userService.findUserIfExistOrElseThrowNotFound(any(Long.class)))
                .thenReturn(userDto);
        Mockito
                .when(itemRequestRepository.findById(any()))
                .thenReturn(Optional.of(itemRequest));
        Mockito
                .when(itemRepository.save(any(Item.class)))
                .thenReturn(item);

        ItemDto itemSaved = itemService.create(itemDto, 1L);

        assertThat(itemSaved.getId(), notNullValue());
        assertThat(itemSaved.getName(), equalTo(itemDto.getName()));
        assertThat(itemSaved.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(itemSaved.getAvailable(), equalTo(itemDto.getAvailable()));
        assertThat(itemSaved.getOwner(), equalTo(itemDto.getOwner()));
    }

    @Test
    void testCreateNewItemNoRequest() {
        Mockito.
                when(userService.findUserIfExistOrElseThrowNotFound(any(Long.class)))
                .thenReturn(userDto);
        Mockito
                .when(itemRepository.save(any(Item.class)))
                .thenReturn(itemNoRequest);

        ItemDto itemSaved = itemService.create(itemDtoNoRequest, 1L);

        assertThat(itemSaved.getId(), notNullValue());
        assertThat(itemSaved.getName(), equalTo(itemDtoNoRequest.getName()));
        assertThat(itemSaved.getDescription(), equalTo(itemDtoNoRequest.getDescription()));
        assertThat(itemSaved.getAvailable(), equalTo(itemDtoNoRequest.getAvailable()));
        assertThat(itemSaved.getOwner(), equalTo(itemDtoNoRequest.getOwner()));
    }

    @Test
    void update() {
        ItemDto itemToUpdate = new ItemDto(
                null, "broken saw", null, false, null, null);
        Item itemUpdated = new Item(
                1L, "broken saw", "big power", false, user, itemRequest);
        Mockito.
                when(userService.findUserIfExistOrElseThrowNotFound(any(Long.class)))
                .thenReturn(userDto);
        Mockito
                .when(itemRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(item));
        Mockito
                .when(itemRepository.save(any(Item.class)))
                .thenReturn(itemUpdated);

        ItemDto itemSaved = itemService.update(itemToUpdate, 1L, 1L);

        assertThat(itemSaved.getId(), notNullValue());
        assertThat(itemSaved.getName(), equalTo(itemToUpdate.getName()));
        assertThat(itemSaved.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(itemSaved.getAvailable(), equalTo(itemToUpdate.getAvailable()));
        assertThat(itemSaved.getOwner(), equalTo(itemDto.getOwner()));
    }

    @Test
    void updateFailWrongUser() {
        ItemDto itemToUpdate = new ItemDto(
                null, "broken saw", null, false, null, null);
        Mockito.
                when(userService.findUserIfExistOrElseThrowNotFound(any(Long.class)))
                .thenReturn(userDto);
        Mockito
                .when(itemRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(item));

        final ForbiddenAccessException exception = Assertions.assertThrows(
                ForbiddenAccessException.class,
                () ->  itemService.update(itemToUpdate, 99L, 1L));

        Assertions.assertEquals("User has no access to edit", exception.getMessage());
    }

    @Test
    void getByItemId() {
        Mockito.
                when(userService.findUserIfExistOrElseThrowNotFound(any(Long.class)))
                .thenReturn(userDto);
        Mockito
                .when(itemRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(item));
        Mockito
                .when(bookingRepository.findFirstByItem_Owner_IdAndItem_IdOrderByStart(anyLong(), anyLong()))
                .thenReturn(Optional.of(last));
        Mockito
                .when(bookingRepository.findFirstByItem_OwnerIdAndIdOrderByStartDesc(anyLong(), anyLong()))
                .thenReturn(Optional.of(next));
        Mockito
                .when(commentRepository.findAllByItemId(anyLong()))
                .thenReturn(List.of(comment));

        ItemBookingCommentDto itemFound = itemService.getByItemId(1L, 1L);

        assertThat(itemFound.getId(), equalTo(item.getId()));
        assertThat(itemFound.getName(), equalTo(item.getName()));
        assertThat(itemFound.getDescription(), equalTo(item.getDescription()));
        assertThat(itemFound.getAvailable(), equalTo(item.getIsAvailable()));
        assertThat(itemFound.getLastBooking(), equalTo(new BookingShortDto(last.getId(), last.getBooker().getId())));
        assertThat(itemFound.getNextBooking(), equalTo(new BookingShortDto(next.getId(), next.getBooker().getId())));
        assertThat(itemFound.getComments().size(), equalTo(1));
    }

    @Test
    void getItemsByUserId() {
        Mockito.
                when(userService.findUserIfExistOrElseThrowNotFound(any(Long.class)))
                .thenReturn(userDto);
        Mockito
                .when(itemRepository.findAllByOwnerId(any(Long.class)))
                .thenReturn(List.of(item));
        Mockito
                .when(bookingRepository.findFirstByItem_Owner_IdAndItem_IdOrderByStart(anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        Mockito
                .when(bookingRepository.findFirstByItem_OwnerIdAndIdOrderByStartDesc(anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        Mockito
                .when(itemRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(item));
        Mockito
                .when(commentRepository.findAllByItemId(anyLong()))
                .thenReturn(Collections.emptyList());

        List<ItemBookingCommentDto> itemsFound = itemService.getItemsByUserId(1L);

        assertThat(itemsFound.size(), equalTo(1));
    }

    @Test
    void search() {
        Mockito
                .when(itemRepository
                        .findAllByNameContainingIgnoreCaseAndIsAvailableTrueOrDescriptionContainingIgnoreCaseAndAndIsAvailableTrue(
                                anyString(), anyString()))
                .thenReturn(Set.of(item));

        Set<ItemDto> items = itemService.search("aw");

        assertThat(items.size(), equalTo(1));
    }

    @Test
    void searchEmptyText() {
        Set<ItemDto> items = itemService.search("");
        assertThat(items.size(), equalTo(0));
    }

    @Test
    void createComment() {
        Mockito
                .when(userService.findUserIfExistOrElseThrowNotFound(any(Long.class)))
                .thenReturn(userDto);
        Mockito.
                when(bookingRepository.findAllByItemIdAndBooker_IdAndEndBefore(
                anyLong(), anyLong(), any()))
                .thenReturn(List.of(booking));
        Mockito
                .when(itemRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(item));
        Mockito
                .when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment);

        CommentDto commentSaved = itemService.createComment(commentDto, 1L, 1L);

        assertThat(commentSaved.getId(), notNullValue());
        assertThat(commentSaved.getText(), equalTo(commentDto.getText()));
    }

    @Test
    void createCommentFailNoBooking() {
        Mockito
                .when(userService.findUserIfExistOrElseThrowNotFound(any(Long.class)))
                .thenReturn(userDto);
        Mockito.
                when(bookingRepository.findAllByItemIdAndBooker_IdAndEndBefore(
                        anyLong(), anyLong(), any()))
                .thenReturn(Collections.emptyList());

        final UnavailableBookingException exception = Assertions.assertThrows(
                UnavailableBookingException.class,
                () ->  itemService.createComment(commentDto, 1L, 1L));

        Assertions.assertEquals("User cannot leave a comment without booking", exception.getMessage());
    }

    @Test
    void getAllByItemId() {
        Mockito
                .when(itemRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(item));
        Mockito
                .when(commentRepository.findAllByItemId(anyLong()))
                .thenReturn(List.of(comment));

        List<Comment> comments = itemService.getAllByItemId(1L);
        assertThat(comments.size(), equalTo(1));

    }

    @Test
    void getItemById() {
        Mockito
                .when(itemRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(item));

        Item itemFound = itemService.getItemById(1L);

        assertThat(itemFound.getId(), notNullValue());
        assertThat(itemFound.getName(), equalTo(item.getName()));
        assertThat(itemFound.getDescription(), equalTo(item.getDescription()));
        assertThat(itemFound.getIsAvailable(), equalTo(item.getIsAvailable()));
        assertThat(itemFound.getOwner(), equalTo(item.getOwner()));
    }

    @Test
    void getItemByWringId() {
        Mockito
                .when(itemRepository.findById(any(Long.class)))
                .thenThrow(new ItemNotFoundException("Item not found - id:  + 99"));

        final ItemNotFoundException exception = Assertions.assertThrows(
                ItemNotFoundException.class,
                () ->  itemService.getItemById(99L));

        Assertions.assertEquals("Item not found - id:  + 99", exception.getMessage());
    }
}