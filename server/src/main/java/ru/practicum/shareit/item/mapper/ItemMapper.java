package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.ItemBookingCommentDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.item.comments.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;


public class ItemMapper {

    public static ItemDto toDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getIsAvailable(),
                new UserDto(item.getOwner().getId(), item.getOwner().getName(), item.getOwner().getEmail()),
                null
        );
    }

    public static Item toItemWithNORequest(ItemDto itemDto) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                User.builder()
                        .id(itemDto.getOwner().getId())
                        .name(itemDto.getOwner().getName())
                        .email(itemDto.getOwner().getEmail())
                        .build(),
                null
        );
    }

    public static ItemDto toDtoWithRequest(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getIsAvailable(),
                new UserDto(item.getOwner().getId(), item.getOwner().getName(), item.getOwner().getEmail()),
                item.getItemRequest().getId());
    }

    public static Item toItemWithRequest(ItemDto itemDto) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                User.builder()
                        .id(itemDto.getOwner().getId())
                        .name(itemDto.getOwner().getName())
                        .email(itemDto.getOwner().getEmail())
                        .build(),
                ItemRequest.builder().id(itemDto.getRequestId()).build()
        );
    }

    public static ItemBookingCommentDto toBookingCommentDto(
            Item item, BookingShortDto lastBooking, BookingShortDto nextBooking, List<CommentDto> comments) {

        return new ItemBookingCommentDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getIsAvailable(),
                lastBooking,
                nextBooking,
                comments
        );
    }
}
