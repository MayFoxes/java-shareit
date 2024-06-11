package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtendedDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ItemDtoMapperTest {
    @Test
    void toOutgoingDtoTest() {
        Item item = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .request(Request.builder()
                        .id(1L)
                        .build())
                .build();
        ItemExtendedDto expected = ItemExtendedDto.builder()
                .id(1L)
                .available(true)
                .description(item.getDescription())
                .name(item.getName())
                .requestId(1L)
                .build();

        ItemExtendedDto actual = ItemMapper.toItemDto(item);

        assertEquals(expected, actual);
    }

    @Test
    void toItemTest() {
        ItemDto dto = ItemDto.builder()
                .available(true)
                .name("name")
                .description("description")
                .build();
        Item expected = Item.builder()
                .available(true)
                .name(dto.getName())
                .description(dto.getDescription())
                .build();

        Item actual = ItemMapper.toItem(dto);

        assertEquals(expected, actual);
    }

    @Test
    void toUpdatedTest() {
        ItemDto dto = ItemDto.builder()
                .available(true)
                .name("name")
                .description("description")
                .build();
        Item expected = Item.builder()
                .available(true)
                .name(dto.getName())
                .description(dto.getDescription())
                .build();

        Item actual = ItemMapper.toUpdatedItem(ItemMapper.toItemDto(expected), dto);

        assertEquals(expected, actual);
    }

}