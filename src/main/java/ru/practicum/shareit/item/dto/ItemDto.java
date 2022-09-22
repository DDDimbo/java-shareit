package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.markerinterface.Create;
import ru.practicum.shareit.markerinterface.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

/**
 * TODO Sprint add-controllers.
 */

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
public class ItemDto {

    @Positive(message = "Id может быть только положительным")
    private Long id;

    @NotBlank(message = "Имя не должно быть пустым", groups = {Create.class})
    @Pattern(regexp = "^\\s*$", groups = {Update.class})
    private String name;

    @NotBlank(message = "Описание не должно быть пустым", groups = {Create.class})
    @Pattern(regexp = "^\\s*$", groups = {Update.class})
    private String description;

    @NotNull(groups = {Create.class})
    private Boolean available;

}
