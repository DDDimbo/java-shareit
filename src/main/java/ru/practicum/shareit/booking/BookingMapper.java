package ru.practicum.shareit.booking;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingPrintDto;
import ru.practicum.shareit.booking.dto.BookingShortInfoDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookingMapper {

    public static Booking toBooking(BookingCreateDto bookingDto, Item item, User booker) {
        return Booking.builder()
                .id(bookingDto.getId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(item)
                .booker(booker)
                .status(bookingDto.getStatus())
                .build();
    }


    public static BookingPrintDto toBookingPrintDto(Booking booking) {
        Item item = booking.getItem();
        User booker = booking.getBooker();
        return BookingPrintDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(item)
                .booker(booker)
                .status(booking.getStatus())
                .build();
    }

    public static BookingShortInfoDto toBookingShortInfoDto(Booking booking) {
        if (booking == null)
            return null;
        Long itemId = booking.getItem().getId();
        Long bookerId = booking.getBooker().getId();
        return BookingShortInfoDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(itemId)
                .bookerId(bookerId)
                .status(booking.getStatus())
                .build();
    }

    public static List<BookingPrintDto> toBookingPrintDtoList(List<Booking> bookingList) {
        return bookingList.stream()
                .map(BookingMapper::toBookingPrintDto)
                .collect(Collectors.toList());
    }
}
