package ru.practicum.shareit.item.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ItemDaoImpl implements ItemDao {

    private final List<Item> items = new ArrayList<>();
    private long id = 0;

    @Override
    public Item create(Item item) {
        item.setId(generateId());
        items.add(item);
        return item;
    }

    @Override
    public Item update(Item item) {
        Item updatedItem = getByItemId(item.getId());

        Optional.ofNullable(item.getName()).ifPresent(updatedItem::setName);
        Optional.ofNullable(item.getDescription()).ifPresent(updatedItem::setDescription);
        Optional.ofNullable(item.getAvailable()).ifPresent(updatedItem::setAvailable);

        return updatedItem;
    }

    @Override
    public Item getByItemId(long itemId) {
        try {
            return items.stream().filter(item -> item.getId() == itemId).findFirst().orElseThrow();
        } catch (NoSuchElementException e) {
            log.error("No items with id {} is found", itemId);
            throw new ItemNotFoundException("Item is not found");
        }
    }

    @Override
    public List<Item> getItemsByUserId(long userId) {
        return items.stream().filter(item -> item.getOwnerId() == userId).collect(Collectors.toList());
    }

    @Override
    public Set<Item> search(String text) {
        List<Item> containsInName = items.stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()))
                .collect(Collectors.toList());
        List<Item> containsInDescription = items.stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .collect(Collectors.toList());
        Set<Item> finalList = new HashSet<>();
        finalList.addAll(containsInDescription);
        finalList.addAll(containsInName);
        return finalList;
    }

    private long generateId() {
        return ++id;
    }
}
