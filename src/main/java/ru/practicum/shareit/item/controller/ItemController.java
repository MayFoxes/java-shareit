package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto saveItem(@RequestHeader("X-Sharer-User-Id") Long ownerId, @Valid @RequestBody ItemDto itemDto) {
        Item tempItem = itemService.saveItem(ownerId, itemDto);
        log.info("A try to create a new item with id:{} by user with id:{}", tempItem, ownerId);
        return ItemMapper.toItemDto(tempItem);
    }

    @GetMapping(path = "/{itemId}")
    public ItemDto findItemById(@PathVariable Long itemId) {
        Item tempItem = itemService.findItemById(itemId);
        log.info("User requested item by id:{}", tempItem);
        return ItemMapper.toItemDto(tempItem);
    }

    @GetMapping("/search")
    public List<ItemDto> findItemsByName(@RequestParam String text) {
        log.info("User search item by name:{}", text);
        return itemService.findItemsByName(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @GetMapping
    public List<ItemDto> findItemByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("User search their items");
        return itemService.findItemByOwner(ownerId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @PatchMapping(path = "/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long ownerId, @PathVariable Long itemId, @RequestBody ItemDto changes) {
        log.info("User with id:{} trying to update a item with id:{}", ownerId, itemId);
        Item tempItem = itemService.updateItem(ownerId, itemId, changes);
        return ItemMapper.toItemDto(tempItem);
    }
}
