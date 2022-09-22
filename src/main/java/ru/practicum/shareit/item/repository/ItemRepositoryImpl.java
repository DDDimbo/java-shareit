package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NonExistedUserIdException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private static final Map<Long, Item> items = new HashMap<>();

    private static long id;


    // Также возвращает id созданного пользователя
    @Override
    public Long save(Item item) {
        id++;
        item.setId(id);
        items.put(id, item);
        return id;
    }

    @Override
    public void put(Long itemId, Item item) {
        items.put(itemId, item);
    }

    @Override
    public void deleteById(Long id) {
        idCheck(id);
        items.remove(id);
    }

    @Override
    public List<Item> findAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public List<Item> findAllForOwner(Long ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public Item getItemById(Long id) {
        idCheck(id);
        return items.get(id);
    }

    private static void idCheck(Long id) {
        if (!items.containsKey(id))
            throw new NonExistedUserIdException("Предмета с таким id не существует");
    }
}
