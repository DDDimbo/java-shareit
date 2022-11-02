package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.markerinterface.Create;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
public class BookingCreateDto {


    @Positive
    private Long id;

    @FutureOrPresent(groups = {Create.class})
    private LocalDateTime start;

    @FutureOrPresent(groups = {Create.class})
    private LocalDateTime end;

    @Positive(groups = {Create.class})
    private Long itemId;

    @NotNull
    private Status status;

}
