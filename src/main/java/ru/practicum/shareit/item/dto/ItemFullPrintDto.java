package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.booking.dto.BookingShortInfoDto;
import ru.practicum.shareit.markerinterface.Create;
import ru.practicum.shareit.markerinterface.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
public class ItemFullPrintDto {

    @Positive(message = "Id может быть только положительным", groups = {Update.class})
    private Long id;

    @NotBlank(message = "Имя не должно быть пустым", groups = {Create.class})
//    @Pattern(regexp = "^\\s*$", groups = {Update.class})
    private String name;

    @NotBlank(message = "Описание не должно быть пустым", groups = {Create.class})
//    @Pattern(regexp = "^\\s*$", groups = {Update.class})
    private String description;

    @NotNull(groups = {Create.class})
    private Boolean available;

    private BookingShortInfoDto lastBooking;

    private BookingShortInfoDto nextBooking;

    private List<CommentPrintView> comments;
}
