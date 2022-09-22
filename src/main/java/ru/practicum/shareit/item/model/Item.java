package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.markerinterface.Update;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * TODO Sprint add-controllers.
 */

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
public class Item {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private User owner;

    private String request;
}
