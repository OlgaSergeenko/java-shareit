package ru.practicum.shareit.request.mapper;

import com.fasterxml.jackson.databind.introspect.TypeResolutionContext;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.stream.Collectors;

public class ItemRequestMapper {

    public static ItemRequestDto toDtoWithItems(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                itemRequest.getItems().stream().map(ItemMapper::toDtoWithRequest).collect(Collectors.toList())
        );
    }

    public static ItemRequestDto toDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
               Collections.emptyList()
        );
    }

    public static ItemRequest toItemRequestNoItems(ItemRequestDto itemRequestDto, Long userId) {
        return new ItemRequest(
                itemRequestDto.getId(),
                itemRequestDto.getDescription(),
                User.builder().id(userId).build(),
                itemRequestDto.getCreated(),
                Collections.emptyList()
        );
    }
}
