package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.markerinterface.Create;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
@Builder
@ToString
public class CommentDto {

    private Long id;

    @NotBlank(groups = Create.class)
    private String text;

    private Long itemId;

    private String authorName;

}
