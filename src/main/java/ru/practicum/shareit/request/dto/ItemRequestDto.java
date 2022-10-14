package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
public class ItemRequestDto {

    @Positive(message = "Id может быть только положительным")
    private Long id;

    @NotBlank(message = "Описание не должно быть пустым")
    private String description;

    private User requester;

    @PastOrPresent
    private LocalDateTime created;
}
