package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto create(ItemRequestDto itemRequestDto, Long userId);

    List<ItemRequestDto> getAllByUserId(Long userId);

    ItemRequestDto getByItemRequestId(Long userId, Long id);

    List<ItemRequestDto> findAllByRequestorIdNot(Long userId, Integer from, Integer size);
}
