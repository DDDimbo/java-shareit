package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NonExistedUserIdException;
import ru.practicum.shareit.exceptions.OwnerAccessException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.utility.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;


    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        if (!userRepository.containsUser(userId))
            throw new NonExistedUserIdException("Пользователя с таким id не существует");
        User owner = userRepository.getUserById(userId);
        Item resItem = ItemMapper.toItem(itemDto, owner);
        Long itemId = itemRepository.save(resItem);
        return ItemMapper.toItemDto(itemRepository.getItemById(itemId));
    }

    @Override
    public ItemDto update(Long ownerId, Long itemId, ItemDto itemDto) {
        if (!userRepository.containsUser(ownerId))
            throw new NonExistedUserIdException("Пользователя с таким id не существует");
        Item item = itemRepository.getItemById(itemId);
        if (!ownerId.equals(item.getOwner().getId()))
            throw new OwnerAccessException("Данный пользователь не является владельцем вещи");

        if (itemDto.getName() != null)
            item.setName(itemDto.getName());
        if (itemDto.getDescription() != null)
            item.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null)
            item.setAvailable(itemDto.getAvailable());
        item.setOwner(userRepository.getUserById(ownerId));

        itemRepository.put(itemId, item);
        return ItemMapper.toItemDto(itemRepository.getItemById(itemId));
    }

    @Override
    public ItemDto findById(Long id) {
        Item item = itemRepository.getItemById(id);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public Collection<ItemDto> findAll(Long ownerId) {
        return itemRepository.findAllForOwner(ownerId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        itemRepository.deleteById(id);
    }

    @Override
    public Collection<ItemDto> search(String text) {
        return itemRepository.findAll()
                .stream()
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(item -> item.getAvailable().equals(true))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
