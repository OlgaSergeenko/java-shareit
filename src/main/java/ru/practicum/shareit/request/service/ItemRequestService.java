package ru.practicum.shareit.request.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@Service
public interface ItemRequestService {

    ItemRequestDto create(ItemRequestDto itemRequestDto, Long userId);

    List<ItemRequestDto> getAllByUserId(Long userId);

    ItemRequestDto getByItemRequestId(Long userId, Long id);

    List<ItemRequestDto> findAllByRequestorIdNot(Long userId, Integer from, Integer size);
}
