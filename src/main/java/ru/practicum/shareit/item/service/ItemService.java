package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentExtendedDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtendedDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item saveItem(Long id, ItemDto item);

    ItemExtendedDto findItemById(Long ownerId, Long itemId);

    List<ItemExtendedDto> findItemsByName(String name);

    List<ItemExtendedDto> findItemsByOwner(Long userId);

    ItemExtendedDto updateItem(Long ownerId, Long itemId, ItemDto changes);

    CommentExtendedDto createComment(CommentDto commentDto, Long itemId, Long userId);

}
