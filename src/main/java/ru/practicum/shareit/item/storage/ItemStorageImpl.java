package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemStorageImpl implements ItemStorage {

    private final UserStorage userRepository;
    private final HashMap<Long, Item> itemMap = new HashMap<>();
    private long id = 0;

    @Override
    public Item saveItem(Long ownerId, ItemDto item) {
        if (userRepository.findUserById(ownerId) == null) {
            throw new NotFoundException("User does not exist");
        }
        Item newItem = ItemMapper.toItem(ownerId, item);
        newItem.setId(++id);
        itemMap.put(newItem.getId(), newItem);
        return newItem;
    }

    @Override
    public Item findItemById(Long itemId) {
        return itemMap.get(itemId);
    }

    @Override
    public List<Item> findItemsByName(String name) {
        if (name.isBlank()) {
            return new ArrayList<>();
        }
        List<Item> tempList = new ArrayList<>();
        for (Long tempId : itemMap.keySet()) {
            if ((itemMap.get(tempId).getName().toLowerCase().contains(name.toLowerCase()) ||
                    itemMap.get(tempId).getDescription().toLowerCase().contains(name.toLowerCase())) &&
                    itemMap.get(tempId).getAvailable()) {
                Item tempItem = itemMap.get(tempId);
                tempList.add(tempItem);
            }
        }
        return tempList;
    }

    @Override
    public List<Item> findItemByOwner(Long userId) {
        List<Item> tempList = new ArrayList<>();
        for (Long tempId : itemMap.keySet()) {
            if (itemMap.get(tempId).getOwner().equals(userId)) {
                Item tempItem = itemMap.get(tempId);
                tempList.add(tempItem);
            }
        }
        return tempList;
    }

    @Override
    public Item updateItem(Long ownerId, Long itemId, ItemDto changes) {
        checkOwner(ownerId, itemId);
        checkItemExist(itemId);
        Item tempItem = itemMap.get(itemId);
        tempItem = Item.builder()
                .id(tempItem.getId())
                .name(changes.getName() == null ? tempItem.getName() : changes.getName())
                .description(changes.getDescription() == null ? tempItem.getDescription() : changes.getDescription())
                .available(changes.getAvailable() == null ? tempItem.getAvailable() : changes.getAvailable())
                .owner(ownerId)
                .request(tempItem.getRequest()).build();
        itemMap.put(itemId, tempItem);
        return tempItem;
    }

    private void checkItemExist(Long itemId) {
        if (itemMap.get(itemId) == null) {
            throw new NotFoundException("No such item with this id");
        }
    }

    private void checkOwner(Long ownerId, Long itemId) {
        if (!itemMap.get(itemId).getOwner().equals(ownerId)) {
            throw new NotFoundException("Only owner can change this");
        }
    }
}
