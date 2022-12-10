package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {

    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> requestItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("Creating request {}, userId={}", itemRequestDto, userId);
        return requestClient.requestItem(userId, itemRequestDto);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable Long requestId) {
        log.info("Get request {}, userId={}", requestId, userId);
        return requestClient.getRequestById(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get requests with userId={}, from={}, size={}", userId, from, size);
        return requestClient.getRequests(userId, from, size);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestsByUserId(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get requests for userId={}", userId);
        return requestClient.getRequestsByUserId(userId);
    }
}
