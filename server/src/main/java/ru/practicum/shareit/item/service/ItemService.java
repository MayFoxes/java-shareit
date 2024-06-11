package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentExtendedDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtendedDto;

import java.util.List;

public interface ItemService {

    ItemExtendedDto saveItem(Long id, ItemDto item);

    ItemExtendedDto findItemById(Long ownerId, Long itemId);

    List<ItemExtendedDto> findItemsByName(String name, Integer from, Integer size);

    List<ItemExtendedDto> findItemsByOwner(Long userId, Integer from, Integer size);

    ItemExtendedDto updateItem(Long ownerId, Long itemId, ItemDto changes);

    CommentExtendedDto createComment(CommentDto commentDto, Long itemId, Long userId);

}
