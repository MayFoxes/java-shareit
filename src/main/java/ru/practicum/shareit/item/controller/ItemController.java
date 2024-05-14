package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentExtendedDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtendedDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public Item saveItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                         @Valid @RequestBody ItemDto itemDto) {
        Item tempItem = itemService.saveItem(ownerId, itemDto);
        log.info("A try to create a new item with id:{} by user with id:{}", tempItem, ownerId);
        return tempItem;
    }

    @GetMapping(path = "/{itemId}")
    public ItemExtendedDto findItemById(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                        @PathVariable Long itemId) {
        log.info("User requested item by id:{}", itemId);
        return itemService.findItemById(ownerId, itemId);
    }

    @GetMapping("/search")
    public List<ItemExtendedDto> findItemsByName(@RequestParam String text) {
        log.info("User search item by name:{}", text);
        return itemService.findItemsByName(text);
    }

    @GetMapping
    public List<ItemExtendedDto> findItemByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("User search their items");
        return itemService.findItemsByOwner(ownerId);
    }

    @PatchMapping(path = "/{itemId}")
    public ItemExtendedDto updateItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                      @PathVariable Long itemId,
                                      @RequestBody ItemDto changes) {
        log.info("User with id:{} trying to update a item with id:{}", ownerId, itemId);
        return itemService.updateItem(ownerId, itemId, changes);
    }

    @PostMapping("/{itemId}/comment")
    public CommentExtendedDto createComment(
            @RequestBody @Valid CommentDto commentDto,
            @PathVariable Long itemId,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.info("Request to create comment.");
        return itemService.createComment(commentDto, itemId, userId);
    }
}
