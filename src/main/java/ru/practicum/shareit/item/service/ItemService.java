package ru.practicum.shareit.item.service;

import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {

    ItemDto create(Long userId, ItemDto itemDto);

    ItemDto findById(Long id);

    Collection<ItemDto> findAll(Long id);

    void deleteById(Long id);

    ItemDto update(Long id, Long itemId, ItemDto itemDto);

    Collection<ItemDto> search(String text);
}
