package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentExtendedDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtendedDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemExtendedDto saveItem(
            @RequestHeader(USER_ID_HEADER) Long ownerId,
            @Valid @RequestBody ItemDto itemDto) {
        ItemExtendedDto tempItem = itemService.saveItem(ownerId, itemDto);
        log.info("A try to create a new item:{}, by user with id:{}", tempItem, ownerId);
        return tempItem;
    }

    @GetMapping("/{itemId}")
    public ItemExtendedDto findItemById(
            @RequestHeader(USER_ID_HEADER) Long ownerId, @PathVariable Long itemId) {
        log.info("User requested item by id:{}", itemId);
        return itemService.findItemById(ownerId, itemId);
    }

    @GetMapping("/search")
    public List<ItemExtendedDto> findItemsByName(@RequestParam String text,
                                                 @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                 @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("User search item by name:{}", text);
        return itemService.findItemsByName(text, from, size);
    }

    @GetMapping
    public List<ItemExtendedDto> findItemByOwner(@RequestHeader(USER_ID_HEADER) Long ownerId,
                                                 @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                 @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("User search their items");
        return itemService.findItemsByOwner(ownerId, from, size);
    }

    @PatchMapping("/{itemId}")
    public ItemExtendedDto updateItem(
            @RequestHeader(USER_ID_HEADER) Long ownerId,
            @PathVariable Long itemId,
            @RequestBody ItemDto changes) {
        log.info("User with id:{} trying to update a item with id:{}", ownerId, itemId);
        return itemService.updateItem(ownerId, itemId, changes);
    }

    @PostMapping("/{itemId}/comment")
    public CommentExtendedDto createComment(
            @RequestBody @Valid CommentDto commentDto,
            @PathVariable Long itemId,
            @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Request to create comment.");
        return itemService.createComment(commentDto, itemId, userId);
    }
}
