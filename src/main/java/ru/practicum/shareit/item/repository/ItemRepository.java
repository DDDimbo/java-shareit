package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    Long save(Item item);

    void deleteById(Long id);

    List<Item> findAll();

    List<Item> findAllForOwner(Long id);

    Item getItemById(Long id);

    void put(Long id, Item item);
}
