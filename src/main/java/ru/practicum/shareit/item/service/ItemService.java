package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemFullPrintDto;

import java.util.List;

public interface ItemService {

    ItemDto create(Long userId, ItemDto itemDto);

    ItemFullPrintDto findById(Long userId, Long itemId);

    List<ItemFullPrintDto> findAll(Long id, Integer from, Integer size);

    void deleteById(Long id);

    ItemDto update(Long id, Long itemId, ItemDto itemDto);

    List<ItemDto> search(String text, Integer from, Integer size);

    CommentDto createComment(Long userId, Long itemId, CommentDto commentDto);
}
