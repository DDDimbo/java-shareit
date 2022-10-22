package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.item.model.Item;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@ToString
@Builder
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

    private List<Item> items;

}
