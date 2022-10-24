package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Set;

public interface ItemDao {

    Item create(Item item);

    Item update(Item item);

    Item getByItemId(long itemId);

    List<Item> getItemsByUserId(long userId);

    Set<Item> search(String text);
}
