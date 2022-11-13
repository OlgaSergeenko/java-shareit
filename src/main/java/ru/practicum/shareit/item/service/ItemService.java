package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comments.Comment;
import ru.practicum.shareit.item.comments.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Set;

public interface ItemService {

    ItemDto create(ItemDto itemDto, Long userId);

    ItemDto update(ItemDto itemDto, Long userId, Long itemId);

    ItemBookingCommentDto getByItemId(Long itemId, Long userId);

    List<ItemBookingCommentDto> getItemsByUserId(Long userId);

    Set<ItemDto> search(String text);

    CommentDto createComment(CommentDto commentDto, Long userId, Long itemId);

    List<Comment> getAllByItemId(Long itemId);

    Item getItemById(Long itemId);
}
