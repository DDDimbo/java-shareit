package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemFullPrintDto;

import java.util.Collection;

public interface ItemService {

    ItemDto create(Long userId, ItemDto itemDto);

    ItemFullPrintDto findById(Long userId, Long itemId);

    Collection<ItemFullPrintDto> findAll(Long id);

    void deleteById(Long id);

    ItemDto update(Long id, Long itemId, ItemDto itemDto);

    Collection<ItemDto> search(String text);

    CommentDto createComment(Long userId, Long itemId, CommentDto commentDto);
}
