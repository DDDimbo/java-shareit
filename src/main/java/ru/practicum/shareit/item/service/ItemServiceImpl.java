package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.exceptions.AccessBookingException;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.OwnerAccessException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentPrintView;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemFullPrintDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.utility.CommentMapper;
import ru.practicum.shareit.item.utility.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.utility.FromSizeRequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;


    @Transactional
    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        if (!userRepository.existsById(userId))
            throw new UserNotFoundException("Пользователя с таким id не существует");
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User с идентификатором " + userId + " не найден."));
        Item resItem = ItemMapper.toItem(itemDto, owner);
        Long itemId = itemRepository.save(resItem).getId();
        return ItemMapper.toItemDto(itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item с идентификатором " + itemId + " не найден.")));
    }


    @Transactional
    @Override
    public CommentDto createComment(Long userId, Long itemId, CommentDto commentDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User с идентификатором " + userId + " не найден."));
        if (!itemRepository.existsById(itemId))
            throw new ItemNotFoundException("Вещи с таким id не существует");
        if (!bookingRepository
                .existsApprovedBookingByBookerAndItemBeforeNow(userId, itemId, List.of(Status.APPROVED, Status.CANCELED)))
            throw new AccessBookingException("Пользователь, который не брал в аренду вещь, не может написать комментарий");
        Comment resComment = commentRepository.save(CommentMapper.toComment(commentDto, user, itemId));
        return CommentMapper.toCommentDto(resComment);
    }


    @Transactional
    @Override
    public ItemDto update(Long ownerId, Long itemId, ItemDto itemDto) {
        if (!userRepository.existsById(ownerId))
            throw new UserNotFoundException("Пользователя с таким id не существует");
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item с идентификатором " + itemId + " не найден."));
        if (!ownerId.equals(item.getOwner().getId()))
            throw new OwnerAccessException("Данный пользователь не является владельцем вещи");

        if (itemDto.getName() != null)
            item.setName(itemDto.getName());
        if (itemDto.getDescription() != null)
            item.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null)
            item.setAvailable(itemDto.getAvailable());
        item.setOwner(userRepository.findById(ownerId).orElseThrow(() ->
                new UserNotFoundException("User с идентификатором " + ownerId + " не найден.")));

        itemRepository.save(item);
        return ItemMapper.toItemDto(itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item с идентификатором " + itemId + " не найден.")));
    }


    @Override
    public ItemFullPrintDto findById(Long userId, Long itemId) {
        if (!userRepository.existsById(userId))
            throw new UserNotFoundException("Пользователя с таким id не существует");
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item с идентификатором " + itemId + " не найден."));
        List<CommentPrintView> comments = commentRepository.findFirst10ByItemIdOrderByCreatedDesc(itemId);

        if (item.getOwner().getId().equals(userId)) {
            Booking lastBooking = bookingRepository.findFirstLastBooking(itemId).orElse(null);
            Booking nextBooking = bookingRepository.findFirstNextBooking(itemId).orElse(null);
            return ItemMapper.toItemFullPrintDtoForOwner(
                    item,
                    BookingMapper.toBookingShortInfoDto(lastBooking),
                    BookingMapper.toBookingShortInfoDto(nextBooking),
                    comments);
        }
        return ItemMapper.toItemFullPrintDtoForUser(item, comments);
    }



    @Override
    public Collection<ItemFullPrintDto> findAll(Long ownerId, Integer from, Integer size) {
        Pageable pageable = FromSizeRequest.of(from, size);
        List<Item> ownerItems = itemRepository.findAllByOwnerIdOrderByIdAsc(ownerId, pageable);
        List<ItemFullPrintDto> resItems = new ArrayList<>();

        for (Item item : ownerItems) {
            Booking lastBooking = null;
            Booking nextBooking = null;
            Long itemId = item.getId();
            List<CommentPrintView> comments = commentRepository.findFirst10ByItemIdOrderByCreatedDesc(itemId);

            if (item.getOwner().getId().equals(ownerId)) {
                lastBooking = bookingRepository.findFirstLastBooking(itemId).orElse(null);
                nextBooking = bookingRepository.findFirstNextBooking(itemId).orElse(null);
            }
            resItems.add(ItemMapper.toItemFullPrintDtoForOwner(
                    item,
                    BookingMapper.toBookingShortInfoDto(lastBooking),
                    BookingMapper.toBookingShortInfoDto(nextBooking),
                    comments)
            );
        }
        return resItems;
    }


    @Transactional
    @Override
    public void deleteById(Long id) {
        itemRepository.deleteById(id);
    }


    @Override
    public Collection<ItemDto> search(String text, Integer from, Integer size) {
        Pageable pageable = FromSizeRequest.of(from, size);

        return itemRepository.itemSearch(text, pageable)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
