package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    Item saveItem(Long id, ItemDto item);

    Item findItemById(Long itemId);

    List<Item> findItemsByName(String name);

    List<Item> findItemByOwner(Long userId);

    Item updateItem(Long ownerId, Long itemId, ItemDto changes);
}
