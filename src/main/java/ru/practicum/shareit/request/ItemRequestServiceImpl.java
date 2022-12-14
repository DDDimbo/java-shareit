package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.ItemRequestNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.utility.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.utility.FromSizeRequest;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    private final ItemRequestRepository itemRequestRepository;

    @Transactional
    @Override
    public ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto) {
        if (!userRepository.existsById(userId))
            throw new UserNotFoundException("Пользователя с таким id не существует");
        ItemRequest resItemRequest = itemRequestRepository.save(ItemRequestMapper.toItemRequest(itemRequestDto, userId));
        return ItemRequestMapper.toItemRequestDto(resItemRequest);
    }

    @Override
    public List<ItemRequestDto> findAllByRequester(Long requesterId) {
        if (!userRepository.existsById(requesterId))
            throw new UserNotFoundException("Пользователя с таким id не существует");

        List<ItemRequestDto> requests = ItemRequestMapper.toItemRequestDtoList(
                itemRequestRepository.findAllByRequesterIdIsOrderByCreatedDesc(requesterId)
        );
        for (ItemRequestDto requestDto : requests) {
            List<ItemDto> items = ItemMapper.toItemDtoList(itemRepository.findAllByRequestId(requestDto.getId()));
            requestDto.setItems(items);
        }
        return requests;
    }

    @Override
    public List<ItemRequestDto> findAll(Long userId, Integer from, Integer size) {
        if (!userRepository.existsById(userId))
            throw new UserNotFoundException("Пользователя с таким id не существует");
        Pageable pageable = FromSizeRequest.of(from, size);
        List<ItemRequestDto> itemRequestDto = ItemRequestMapper.toItemRequestDtoList(
                itemRequestRepository.findAllByRequesterIdNotOrderByCreatedDesc(userId, pageable)
        );
        for (ItemRequestDto requestDto : itemRequestDto) {
            List<ItemDto> items = ItemMapper.toItemDtoList(itemRepository.findAllByRequestId(requestDto.getId()));
            requestDto.setItems(items);
        }
        return itemRequestDto;
    }

    @Override
    public ItemRequestDto findById(Long userId, Long requestId) {
        if (!userRepository.existsById(userId))
            throw new UserNotFoundException("Пользователя с таким id не существует");
        if (!itemRequestRepository.existsById(requestId))
            throw new ItemRequestNotFoundException("Запроса с таким id не существует");

        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new ItemRequestNotFoundException("Запроса с " + requestId + " не существует")));

        List<ItemDto> items = ItemMapper.toItemDtoList(itemRepository.findAllByRequestId(requestId));
        itemRequestDto.setItems(items);
        return itemRequestDto;
    }
}
