package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Builder
@ToString
public class CommentDto {

    private Long id;

    private String text;

    private Long itemId;

    private String authorName;

}
