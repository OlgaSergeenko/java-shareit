package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comments.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@RestController
@RequestMapping("/items")
@AllArgsConstructor
@Slf4j
public class ItemController {

    private ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> create(@Valid @RequestBody ItemDto itemDto,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        ItemDto itemSaved = itemService.create(itemDto, userId);
        log.info(String.format("Item with id %d is created", itemSaved.getId()));
        Optional.ofNullable(itemDto.getRequestId()).ifPresent(itemSaved::setRequestId);
        return ResponseEntity.ok(itemSaved);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ItemDto> update(@RequestBody ItemDto itemDto,
                                          @RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable("id") Long itemId) {
        itemDto = itemService.update(itemDto, userId, itemId);
        log.info(String.format("Item with id %d is updated", itemDto.getId()));
        return ResponseEntity.ok(itemDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemBookingCommentDto> getItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                             @PathVariable("id") Long itemId) {
        return ResponseEntity.ok(itemService.getByItemId(itemId, userId));
    }

    @GetMapping()
    public ResponseEntity<List<ItemBookingCommentDto>> getItemsByUserId(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(itemService.getItemsByUserId(userId));
    }

    @GetMapping("/search")
    public ResponseEntity<Set<ItemDto>> searchItemsByQuery(@RequestParam String text) {
        return ResponseEntity.ok(itemService.search(text));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> createComment(@Valid @RequestBody CommentDto commentDto,
                                                    @RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @PathVariable("itemId") Long itemId) {
        CommentDto commentSaved = itemService.createComment(commentDto, userId, itemId);
        log.info("Comment created" + commentSaved.getId());
        return ResponseEntity.ok(commentSaved);
    }
}
