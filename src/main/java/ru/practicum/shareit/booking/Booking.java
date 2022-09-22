package ru.practicum.shareit.booking;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.markerinterface.Update;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class Booking {

    @Positive(message = "Id может быть только положительным")
    private Long id;

    @PastOrPresent
    private LocalDateTime start;

    @PastOrPresent
    private LocalDateTime end;

    private Item item;

    @NotNull(message = "Booker не может быть null", groups = Update.class)
    private User booker;

    @NotNull(message = "Status не может быть null", groups = Update.class)
    private Status status;
}
