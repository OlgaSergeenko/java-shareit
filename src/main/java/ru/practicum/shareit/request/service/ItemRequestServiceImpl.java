package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.RequestNotFoundException;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private ItemRequestRepository itemRequestRepository;
    private UserService userService;

    public ItemRequestDto create(ItemRequestDto itemRequestDto, Long userId) {
        userService.findUserIfExistOrElseThrowNotFound(userId);
        itemRequestDto.setCreated(LocalDateTime.now());
        ItemRequest itemRequest = ItemRequestMapper.toItemRequestNoItems(itemRequestDto, userId);
        return ItemRequestMapper.toDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getAllByUserId(Long userId) {
        userService.findUserIfExistOrElseThrowNotFound(userId);
        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorId(userId);
        return mapToDtoRequestsWithItemsAndWithout(requests);
    }

    @Override
    public ItemRequestDto getByItemRequestId(Long userId, Long id) {
        userService.findUserIfExistOrElseThrowNotFound(userId);
        Optional<ItemRequest> request = itemRequestRepository.findById(id);
        if (request.isEmpty()) {
            throw new RequestNotFoundException(String.format("Request %d not found", id));
        }
        ItemRequest requestFound = request.get();
        if (requestFound.getItems() != null) {
            return ItemRequestMapper.toDtoWithItems(requestFound);
        }
        return ItemRequestMapper.toDto(requestFound);
    }

    @Override
    public List<ItemRequestDto> findAllByRequestorIdNot(Long userId, Integer from, Integer size) {
        Pageable page = PageRequest.of(from / size, size);
        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorIdNotOrderByCreatedDesc(userId, page);
        return mapToDtoRequestsWithItemsAndWithout(requests);
    }

    private List<ItemRequestDto> mapToDtoRequestsWithItemsAndWithout(List<ItemRequest> requests) {
        List<ItemRequestDto> totalRequests = new ArrayList<>();
        List<ItemRequestDto> requestsNoItems = requests
                .stream()
                .filter(r -> r.getItems().isEmpty())
                .map(ItemRequestMapper::toDto)
                .collect(Collectors.toList());
        List<ItemRequestDto> requestWithItems = requests
                .stream()
                .filter(r -> !r.getItems().isEmpty())
                .map(ItemRequestMapper::toDtoWithItems)
                .collect(Collectors.toList());
        totalRequests.addAll(requestsNoItems);
        totalRequests.addAll(requestWithItems);
        return totalRequests;
    }
}
