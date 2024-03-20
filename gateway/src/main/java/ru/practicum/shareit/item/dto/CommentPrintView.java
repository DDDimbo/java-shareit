package ru.practicum.shareit.item.dto;

import java.time.LocalDateTime;

public interface CommentPrintView {


    Long getId();

    String getText();

    Long getItemId();

    String getAuthorName();

    LocalDateTime getCreated();
}
