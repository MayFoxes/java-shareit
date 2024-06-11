package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient client;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createItem(
            @Valid @RequestBody ItemDto dto,
            @RequestHeader(USER_ID_HEADER) Long owner
    ) {
        log.info("Request to create item: {}", dto);
        return client.create(owner, dto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @RequestBody ItemDto dto,
            @PathVariable Long itemId,
            @RequestHeader(USER_ID_HEADER) Long user
    ) {
        log.info("Request to update item {}.", itemId);
        return client.update(itemId, user, dto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(
            @PathVariable Long itemId,
            @RequestHeader(USER_ID_HEADER) Long user
    ) {
        log.info("Request to get item {}.", itemId);
        return client.getById(user, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItems(@RequestHeader(USER_ID_HEADER) Long owner,
                                               @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                               @RequestParam(defaultValue = "10") @Positive Integer size
    ) {
        log.info("Request to receive user {}' items.", owner);
        return client.getUserItems(owner, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(
            @RequestParam String text,
            @RequestHeader(USER_ID_HEADER) Long user,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size
    ) {
        log.info("Request to search items by \"{}\".", text);
        return client.searchByText(user, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(
            @Valid @RequestBody CommentDto dto,
            @PathVariable Long itemId,
            @RequestHeader(USER_ID_HEADER) Long userId
    ) {
        log.info("Request to create comment.");
        return client.createComment(userId, itemId, dto);
    }
}
