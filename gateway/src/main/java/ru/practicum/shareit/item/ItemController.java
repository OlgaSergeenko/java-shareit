package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemCreateRequestDto;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> saveItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestBody @Valid ItemCreateRequestDto itemCreateRequestDto) {
        log.info("Creating item {}, userId={}", itemCreateRequestDto, userId);
        return itemClient.saveNewItem(userId, itemCreateRequestDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @PathVariable Long itemId) {
        log.info("Get item Id={}", itemId);
        return itemClient.getItem(userId, itemId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItemById(@RequestBody ItemCreateRequestDto itemDto,
                                                 @RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable("id") Long itemId) {
        log.info("Updating item Id={}", itemId);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping()
    public ResponseEntity<Object> getItemsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Getting item for userID={}", userId);
        return itemClient.getItemByUserId(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemsByQuery(@RequestParam String text) {
        log.info("Searching for items with {}", text);
        return itemClient.searchItemByQuery(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> saveComment(@Valid @RequestBody CommentRequestDto commentDto,
                                              @RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                              @PathVariable("itemId") Long itemId) {
        log.info("Creating comment = {} for item = {}", commentDto, itemId);
        return itemClient.saveNewComment(commentDto, userId, itemId);
    }
}
