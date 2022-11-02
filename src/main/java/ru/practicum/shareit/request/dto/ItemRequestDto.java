package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemRequestDto {

    @Positive(message = "Id может быть только положительным")
    private Long id;

    @NotBlank(message = "Описание не должно быть пустым")
    private String description;

    @Positive
    private Long requesterId;

    @PastOrPresent
    private LocalDateTime created;

    private List<ItemDto> items;

}
