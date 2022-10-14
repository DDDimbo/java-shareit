package ru.practicum.shareit.item.utility;

import ru.practicum.shareit.booking.dto.BookingShortInfoDto;
import ru.practicum.shareit.item.dto.CommentPrintView;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemFullPrintDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

public class ItemMapper {

    public static Item toItem(ItemDto itemDto, User user) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(user)
                .build();
    }

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static ItemFullPrintDto toItemFullPrintDtoForUser(Item item, List<CommentPrintView> comments) {
        return ItemFullPrintDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(null)
                .nextBooking(null)
                .comments(comments)
                .build();
    }

    public static ItemFullPrintDto toItemFullPrintDtoForOwner(Item item,
                                                              BookingShortInfoDto lastBooking,
                                                              BookingShortInfoDto nextBooking,
                                                              List<CommentPrintView> comments) {
        return ItemFullPrintDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(comments)
                .build();
    }

}
