package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenAccessException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class ItemService {

    private final ItemDao itemDao;
    private final UserService userService;

    public Item create(Item item, long userId) {
        userService.getById(userId);
        item.setOwnerId(userId);
        return itemDao.create(item);
    }

    public Item update(Item item, long userId) {
        Item itemToUpdate = getByItemId(item.getId());
        if (itemToUpdate.getOwnerId() != userId) {
            throw new ForbiddenAccessException("User has no access to edit");
        }
        return itemDao.update(item);
    }

    public Item getByItemId(long itemId) {
        return itemDao.getByItemId(itemId);
    }

    public List<Item> getItemsByUserId(long userId) {
        userService.getById(userId);
        return itemDao.getItemsByUserId(userId);
    }

    public Set<Item> search(String text) {

        if (StringUtils.isBlank(text)) {
            return Collections.emptySet();
        }
        return itemDao.search(text);
    }
}
