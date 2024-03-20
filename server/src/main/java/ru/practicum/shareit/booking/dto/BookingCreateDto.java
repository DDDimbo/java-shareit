package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.enums.Status;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
public class BookingCreateDto {


    private Long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private Long itemId;

    private Status status;

}
