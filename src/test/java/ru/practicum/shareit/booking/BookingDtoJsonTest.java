package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingPrintDto;
import ru.practicum.shareit.booking.dto.BookingShortInfoDto;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingCreateDto> jsonCreate;

    @Autowired
    private JacksonTester<BookingPrintDto> jsonPrint;

    @Autowired
    private JacksonTester<BookingShortInfoDto> jsonShortInfo;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    private final User user = new User(1L, "updateName", "updateName@user.com");

    private final User owner = new User(2L, "name", "name@user.com");

    private final Item item = new Item(1L, "Аккумуляторная дрель",
            "Аккумуляторная дрель", true, owner, null);

    private final LocalDateTime start = LocalDateTime.now().plusDays(1);
    private final LocalDateTime end = LocalDateTime.now().plusDays(3);

    private final BookingPrintDto bookingPrintDto = BookingPrintDto.builder()
            .id(1L)
            .start(start)
            .end(end)
            .item(item)
            .booker(user)
            .status(Status.APPROVED)
            .build();


    private final BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
            .id(1L)
            .start(start)
            .end(end)
            .itemId(1L)
            .status(Status.APPROVED)
            .build();

    private final BookingShortInfoDto bookingShortInfoDto = BookingShortInfoDto.builder()
            .id(1L)
            .start(start)
            .end(end)
            .itemId(1L)
            .bookerId(1L)
            .status(Status.APPROVED)
            .build();


    @Test
    void testBookingPrintDto() throws Exception {
        JsonContent<BookingPrintDto> result = jsonPrint.write(bookingPrintDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(start.format(formatter));
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(end.format(formatter));
        assertThat(result).hasJsonPathValue("$.item");
        assertThat(result).hasJsonPathValue("$.booker");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(String.valueOf(Status.APPROVED));
    }

    @Test
    void testBookingCreateDto() throws Exception {
        JsonContent<BookingCreateDto> result = jsonCreate.write(bookingCreateDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(start.format(formatter));
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(end.format(formatter));
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(String.valueOf(Status.APPROVED));
    }

    @Test
    void testBookingShortInfoDto() throws Exception {
        JsonContent<BookingShortInfoDto> result = jsonShortInfo.write(bookingShortInfoDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(start.format(formatter));
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(end.format(formatter));
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(String.valueOf(Status.APPROVED));
    }
}