package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtendedDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

@ExtendWith(MockitoExtension.class)
public class ItemDtoMapperTest {
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
    void toOutgoingDtoListTest() {
        Item item1 = Item.builder()
                .id(1L)
                .name("name1")
                .description("description1")
                .available(true)
                .request(Request.builder()
                        .id(1L)
                        .build())
                .build();
        Item item2 = Item.builder()
                .id(2L)
                .name("name2")
                .description("description2")
                .available(true)
                .request(Request.builder()
                        .id(2L)
                        .build())
                .build();
        ItemExtendedDto expected1 = ItemExtendedDto.builder()
                .id(1L)
                .available(true)
                .description(item1.getDescription())
                .name(item1.getName())
                .requestId(1L)
                .build();
        ItemExtendedDto expected2 = ItemExtendedDto.builder()
                .id(2L)
                .available(true)
                .description(item2.getDescription())
                .name(item2.getName())
                .requestId(2L)
                .build();
        List<Item> items = List.of(item1, item2);
        List<ItemExtendedDto> expected = List.of(expected1, expected2);

        List<ItemExtendedDto> actual = Stream.of(item1, item2)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());

        assertIterableEquals(expected, actual);
    }

    @Test
    void toItemListTest() {
        ItemDto dto1 = ItemDto.builder()
                .available(true)
                .name("name1")
                .description("description1")
                .build();
        Item expected1 = Item.builder()
                .available(true)
                .name(dto1.getName())
                .description(dto1.getDescription())
                .build();
        ItemDto dto2 = ItemDto.builder()
                .available(true)
                .name("name2")
                .description("description2")
                .build();
        Item expected2 = Item.builder()
                .available(true)
                .name(dto2.getName())
                .description(dto2.getDescription())
                .build();
        List<ItemDto> dtos = List.of(dto1, dto2);
        List<Item> expected = List.of(expected1, expected2);

        List<Item> actual = dtos.stream()
                .map(ItemMapper::toItem)
                .collect(Collectors.toList());

        assertIterableEquals(expected, actual);
    }
}