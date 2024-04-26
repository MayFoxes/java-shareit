package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;

    @Override
    public Item saveItem(Long itemId, ItemDto item) {
        return itemStorage.saveItem(itemId, item);
    }

    @Override
    public Item findItemById(Long itemId) {
        return itemStorage.findItemById(itemId);
    }

    @Override
    public List<Item> findItemsByName(String name) {
        return itemStorage.findItemsByName(name);
    }

    @Override
    public List<Item> findItemByOwner(Long ownerId) {
        return itemStorage.findItemByOwner(ownerId);
    }

    @Override
    public Item updateItem(Long ownerId, Long itemId, ItemDto changes) {
        return itemStorage.updateItem(ownerId, itemId, changes);
    }
}
