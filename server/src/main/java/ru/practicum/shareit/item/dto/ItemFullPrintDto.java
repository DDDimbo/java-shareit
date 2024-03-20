package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.booking.dto.BookingShortInfoDto;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
public class ItemFullPrintDto {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private BookingShortInfoDto lastBooking;

    private BookingShortInfoDto nextBooking;

    private List<CommentPrintView> comments;
}
