package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.ItemBookingCommentDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.exception.ForbiddenAccessException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UnavailableBookingException;
import ru.practicum.shareit.item.comments.Comment;
import ru.practicum.shareit.item.comments.CommentDto;
import ru.practicum.shareit.item.comments.CommentMapper;
import ru.practicum.shareit.item.comments.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Getter
@Setter
@AllArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private ItemRepository itemRepository;
    private UserService userService;
    private BookingRepository bookingRepository;
    private CommentRepository commentRepository;
    private ItemRequestRepository itemRequestRepository;

    @Override
    public ItemDto create(ItemDto itemDto, Long userId) {
        UserDto userDto = userService.findUserIfExistOrElseThrowNotFound(userId);
        itemDto.setOwner(userDto);
        Long requestId = itemDto.getRequestId();
        if (requestId == null) {
            Item item = ItemMapper.toItemWithNORequest(itemDto);
            Item itemSaved = itemRepository.save(item);
            return ItemMapper.toDto(itemSaved);
        }
        Item item = ItemMapper.toItemWithRequest(itemDto);
        Optional<ItemRequest> request = itemRequestRepository.findById(requestId);
        request.ifPresent(item::setItemRequest);
        Item itemSaved = itemRepository.save(item);
        return ItemMapper.toDto(itemSaved);
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long userId, Long itemId) {
        userService.findUserIfExistOrElseThrowNotFound(userId);
        Item item = getItemById(itemId);
        if (!Objects.equals(item.getOwner().getId(), userId)) {
            throw new ForbiddenAccessException("User has no access to edit");
        }

        Optional.ofNullable(itemDto.getName()).ifPresent(item::setName);
        Optional.ofNullable(itemDto.getDescription()).ifPresent(item::setDescription);
        Optional.ofNullable(itemDto.getAvailable()).ifPresent(item::setIsAvailable);

        return ItemMapper.toDto(itemRepository.save(item));
    }

    @Override
    public ItemBookingCommentDto getByItemId(Long itemId, Long userId) {
        userService.findUserIfExistOrElseThrowNotFound(userId);
        getItemById(itemId);
        Item item = getItemById(itemId);
        BookingShortDto lastBooking = null;
        BookingShortDto nextBooking = null;
        Optional<Booking> last = bookingRepository.findFirstByItem_Owner_IdAndItem_IdOrderByStart(userId, itemId);
        if (last.isPresent()) {
            lastBooking = BookingMapper.toShortDto(last.get());
        }
        Optional<Booking> next = bookingRepository.findFirstByItem_OwnerIdAndIdOrderByStartDesc(userId, itemId);
        if (next.isPresent()) {
            nextBooking = BookingMapper.toShortDto(next.get());
        }

        List<Comment> comments = getAllByItemId(itemId);
        if (comments.isEmpty()) {
            return ItemMapper.toBookingCommentDto(item, lastBooking, nextBooking, Collections.emptyList());
        }
        List<CommentDto> commentsDto = comments.stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());

        return ItemMapper.toBookingCommentDto(item, lastBooking, nextBooking, commentsDto);
    }

    @Override
    public List<ItemBookingCommentDto> getItemsByUserId(Long userId) {
        userService.findUserIfExistOrElseThrowNotFound(userId);
        List<Item> items = itemRepository.findAllByOwnerId(userId);
        if (items.isEmpty()) {
            throw new ItemNotFoundException("User has no items");
        }
        List<ItemBookingCommentDto> itemsWithBooking = new ArrayList<>();
        for (Item item : items) {
            BookingShortDto lastBooking = null;
            BookingShortDto nextBooking = null;
            Optional<Booking> last = bookingRepository.findFirstByItem_Owner_IdAndItem_IdOrderByStart(userId, item.getId());
            Optional<Booking> next = bookingRepository.findFirstByItem_OwnerIdAndIdOrderByStartDesc(userId, item.getId());
            if (last.isPresent()) {
                lastBooking = BookingMapper.toShortDto(last.get());
            }
            if (next.isPresent()) {
                nextBooking = BookingMapper.toShortDto(next.get());
            }
            List<Comment> comments = getAllByItemId(item.getId());
            if (comments.isEmpty()) {
                itemsWithBooking.add(ItemMapper.toBookingCommentDto(item, lastBooking, nextBooking, null));
            } else {
                List<CommentDto> commentsDto = comments.stream()
                        .map(CommentMapper::toDto)
                        .collect(Collectors.toList());
                itemsWithBooking.add(ItemMapper.toBookingCommentDto(item, lastBooking, nextBooking, commentsDto));
            }
        }
        return itemsWithBooking;
    }

    @Override
    public Set<ItemDto> search(String text) {
        if (StringUtils.isBlank(text)) {
            return Collections.emptySet();
        }
        return itemRepository
                .findAllByNameContainingIgnoreCaseAndIsAvailableTrueOrDescriptionContainingIgnoreCaseAndAndIsAvailableTrue(
                        text, text)
                .stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toSet());
    }

    @Override
    public CommentDto createComment(CommentDto commentDto, Long userId, Long itemId) {
        UserDto userDto = userService.findUserIfExistOrElseThrowNotFound(userId);
        User user = UserMapper.toUser(userDto);
        List<Booking> bookings = bookingRepository.findAllByItemIdAndBooker_IdAndEndBefore(
                itemId, userId, LocalDateTime.now());
        if (bookings.isEmpty() || commentDto.getText().isEmpty()) {
            throw new UnavailableBookingException("User cannot leave a comment without booking");
        }

        Item item = getItemById(itemId);
        Comment comment = CommentMapper.toComment(commentDto, item, user);
        comment.setCreationDate(LocalDateTime.now());
        return CommentMapper.toDto(commentRepository.save(comment));
    }

    @Override
    public List<Comment> getAllByItemId(Long itemId) {
        getItemById(itemId);
        return commentRepository.findAllByItemId(itemId);
    }

    @Override
    public Item getItemById(Long itemId) {
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isEmpty()) {
            throw new ItemNotFoundException("Item not found - id: " + itemId);
        }
        return item.get();
    }
}
