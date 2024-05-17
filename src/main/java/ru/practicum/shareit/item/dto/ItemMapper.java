package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.model.Item;

@UtilityClass
public class ItemMapper {
    public ItemExtendedDto toItemDto(Item item) {
        return ItemExtendedDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public Item toItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(itemDto.getOwner())
                .build();
    }

    public Item toUpdatedItem(Long ownerId, ItemExtendedDto itemDto, ItemDto changes) {

        return Item.builder()
                .id(itemDto.getId())
                .name(changes.getName() == null ? itemDto.getName() : changes.getName())
                .description(changes.getDescription() == null ? itemDto.getDescription() : changes.getDescription())
                .available(changes.getAvailable() == null ? itemDto.getAvailable() : changes.getAvailable())
                .owner(ownerId)
                .build();
    }
}
