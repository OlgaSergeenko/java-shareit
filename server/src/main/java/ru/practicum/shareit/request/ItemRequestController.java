package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@Validated
@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
@Slf4j
public class ItemRequestController {

    private final ItemRequestService requestService;

    @PostMapping
    public ResponseEntity<ItemRequestDto> create(@RequestBody ItemRequestDto itemRequestDto,
                                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        ItemRequestDto requestDtoSaved = requestService.create(itemRequestDto, userId);
        log.info(String.format("Request with id %d is created", requestDtoSaved.getId()));
        return ResponseEntity.ok(requestDtoSaved);
    }

    @GetMapping()
    public ResponseEntity<List<ItemRequestDto>> getItemRequestByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(requestService.getAllByUserId(userId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDto>> getAllRequestsByPages(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(required = false, defaultValue = "0") Integer from,
            @RequestParam(required = false, defaultValue = "20") Integer size) {
        return ResponseEntity.ok(requestService.findAllByRequestorIdNot(userId, from, size));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDto> getItemRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                             @PathVariable Long requestId) {
        return ResponseEntity.ok(requestService.getByItemRequestId(userId, requestId));
    }
}
