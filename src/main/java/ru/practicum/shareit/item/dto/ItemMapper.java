package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;

public class ItemMapper {
    public static ItemExtendedDto toItemDto(Item item) {
        return ItemExtendedDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static Item toItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(itemDto.getOwner())
                .build();
    }

    public static Item toUpdatedItem(Long ownerId, ItemExtendedDto itemDto, ItemDto changes) {

        return Item.builder()
                .id(itemDto.getId())
                .name(changes.getName() == null ? itemDto.getName() : changes.getName())
                .description(changes.getDescription() == null ? itemDto.getDescription() : changes.getDescription())
                .available(changes.getAvailable() == null ? itemDto.getAvailable() : changes.getAvailable())
                .owner(ownerId)
                .build();
    }
}
