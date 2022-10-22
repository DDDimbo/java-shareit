package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Collection;

public interface ItemRequestService {

    ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto);
    Collection<ItemRequestDto> findAllByRequester(Long ownerId);
    Collection<ItemRequestDto> findAll(Long ownerId, Integer from, Integer size);
    ItemRequestDto findById(Long userId, Long requestId);
}
