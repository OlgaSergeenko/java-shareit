package ru.practicum.shareit.request.service;

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
import ru.practicum.shareit.exception.RequestNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
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
class ItemRequestServiceImplTest {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd, HH:mm:ss");
    private final String created = LocalDateTime.now().plusMinutes(2).format(formatter);
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserService userService;
    private UserDto userDto;
    private User user;
    private ItemRequestDto itemRequestDto;
    private ItemRequest itemRequest;
    private ItemRequest itemRequest2;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Olga", "Olga@email.com");
        User requestor = new User(2L, "Pasha", "Lalal@email.com");
        userDto = new UserDto(1L, "Olga", "Olga@email.com");
        itemRequest = new ItemRequest(
                1L, "text", requestor, LocalDateTime.parse(created, formatter), Collections.emptyList());
        itemRequest2 = new ItemRequest(
                2L, "new", requestor, LocalDateTime.parse(created, formatter), Collections.emptyList());
        itemRequestDto = ItemRequestDto.builder()
                .description("blablabla")
                .build();
    }

    @Test
    void create() {
        Mockito
                .when(userService.findUserIfExistOrElseThrowNotFound(any(Long.class)))
                .thenReturn(userDto);
        Mockito
                .when(itemRequestRepository.save(any(ItemRequest.class)))
                .thenReturn(itemRequest);

        ItemRequestDto itemRequestSaved = itemRequestService.create(itemRequestDto, 1L);

        assertThat(itemRequestSaved.getId(), notNullValue());
        assertThat(itemRequestSaved.getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(String.valueOf(itemRequestSaved.getCreated()), equalTo(String.valueOf(itemRequest.getCreated())));
        assertThat(itemRequestSaved.getItems(), equalTo(itemRequest.getItems()));
    }

    @Test
    void getAllByUserId() {
        Item item = new Item(1L, "saw", "big power", true, user, itemRequest2);
        itemRequest2.setItems(List.of(item));
        Mockito
                .when(itemRequestRepository.findAllByRequestorId(anyLong()))
                .thenReturn(List.of(itemRequest, itemRequest2));

        List<ItemRequestDto> requests = itemRequestService.getAllByUserId(2L);

        assertThat(requests.size(), equalTo(2));
        assertThat(requests.get(0).getId(), equalTo(itemRequest.getId()));
        assertThat(requests.get(1).getId(), equalTo(itemRequest2.getId()));
    }

    @Test
    void getByItemRequestIdNoItems() {
        Mockito
                .when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemRequest));

        ItemRequestDto requestFound = itemRequestService.getByItemRequestId(2L, 1L);

        assertThat(requestFound.getId(), notNullValue());
        assertThat(requestFound.getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(String.valueOf(requestFound.getCreated()), equalTo(String.valueOf(itemRequest.getCreated())));
        assertThat(requestFound.getItems(), equalTo(itemRequest.getItems()));
    }

    @Test
    void getByItemRequestIdWithItems() {
        Item item = new Item(1L, "saw", "big power", true, user, itemRequest2);
        itemRequest2.setItems(List.of(item));
        Mockito
                .when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemRequest2));

        ItemRequestDto requestFound = itemRequestService.getByItemRequestId(2L, 1L);

        assertThat(requestFound.getId(), notNullValue());
        assertThat(requestFound.getDescription(), equalTo(itemRequest2.getDescription()));
        assertThat(String.valueOf(requestFound.getCreated()), equalTo(String.valueOf(itemRequest2.getCreated())));
        assertThat(requestFound.getItems().size(), equalTo(itemRequest2.getItems().size()));
    }

    @Test
    void failGetByItemRequestIdNotFound() {
        Mockito
                .when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        final RequestNotFoundException exception = Assertions.assertThrows(
                RequestNotFoundException.class,
                () -> itemRequestService.getByItemRequestId(1L, 1L));

        Assertions.assertEquals("Request 1 not found", exception.getMessage());
    }

    @Test
    void findAllByRequestorIdNot() {
        Item item = new Item(1L, "saw", "big power", true, user, itemRequest2);
        itemRequest2.setItems(List.of(item));
        Mockito
                .when(itemRequestRepository.findAllByRequestorIdNotOrderByCreatedDesc(anyLong(), any()))
                .thenReturn(List.of(itemRequest, itemRequest2));

        List<ItemRequestDto> requests = itemRequestService.findAllByRequestorIdNot(1L, 0, 2);

        assertThat(requests.size(), equalTo(2));
        assertThat(requests.get(0).getId(), equalTo(itemRequest.getId()));
        assertThat(requests.get(1).getId(), equalTo(itemRequest2.getId()));
    }
}
