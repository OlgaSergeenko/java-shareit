package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> create(@Valid @RequestBody ItemDto itemDto,
                                          @RequestHeader("X-Sharer-User-Id") long userId) {
        Item item = ItemMapper.toItem(itemDto);
        itemService.create(item, userId);
        return ResponseEntity.ok(ItemMapper.toDto(item));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ItemDto> update(@RequestBody ItemDto itemDto,
                                          @RequestHeader("X-Sharer-User-Id") long userId,
                                          @PathVariable("id") long itemId) {
        Item item = ItemMapper.toItem(itemDto);
        item.setId(itemId);
        item = itemService.update(item, userId);
        return ResponseEntity.ok(ItemMapper.toDto(item));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDto> getItemById(@PathVariable("id") long itemId) {
        Item item = itemService.getByItemId(itemId);
        return ResponseEntity.ok(ItemMapper.toDto(item));
    }

    @GetMapping()
    public ResponseEntity<List<ItemDto>> getItemsByUserId(@RequestHeader("X-Sharer-User-Id") long userId) {
        return ResponseEntity.ok(itemService.getItemsByUserId(userId).stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList()));
    }

    @GetMapping("/search")
    public ResponseEntity<Set<ItemDto>> searchItemsByQuery(@RequestParam String text) {
        return ResponseEntity.ok(itemService.search(text).stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toSet()));
    }
}
