package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingPrintDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.utility.FromSizeRequest;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.enums.State.*;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;


    @Transactional
    @Override
    public BookingPrintDto create(Long bookerId, BookingCreateDto bookingDto) {
        if (!userRepository.existsById(bookerId))
            throw new UserNotFoundException("Пользователя с таким id не существует");
        dateTimeCheck(bookingDto.getStart(), bookingDto.getEnd());

        Long itemId = bookingDto.getItemId();
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(
                        "Item с идентификатором " + itemId + " не найден."
                ));
        if (item.getOwner().getId().equals(bookerId))
            throw new BookingAccessException("Владелец не может создать бронь на свою вещь");
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new UserNotFoundException("User с идентификатором " + bookerId + " не найден."));

        if (!item.getAvailable())
            throw new AccessErrorForItemException("Вещь с указанным id недоступна для запроса на бронирование.");
        bookingDto.setStatus(Status.WAITING);
        Booking resBooking = bookingRepository.save(BookingMapper.toBooking(bookingDto, item, booker));
        return BookingMapper.toBookingPrintDto(bookingRepository.findById(resBooking.getId())
                .orElseThrow(() -> new BookingNotFoundException(
                        "Booking с идентификатором " + resBooking.getId() + " не найден.")));
    }

    private static void dateTimeCheck(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end) || start.equals(end))
            throw new DateTimeException("StartTime не может быть после EndTime или равняться ему");
    }

    @Transactional
    @Override
    public BookingPrintDto approve(Long userId, Long bookingId, Boolean approved) {
        if (!userRepository.existsById(userId))
            throw new UserNotFoundException("Пользователя с таким id не существует");
        if (!bookingRepository.existsById(bookingId))
            throw new BookingNotFoundException("Брони с таким id не существует");
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(
                        "Booking с идентификатором " + bookingId + " не найден."
                ));

        Item item = booking.getItem();
        if (!item.getOwner().getId().equals(userId))
            throw new UserAccessException("Данный пользователь не может управлять запрашиваемой бронью.");

        if (booking.getStatus().equals(Status.APPROVED))
            throw new AlreadyExistsStatusException("Статус уже подтвержден");

        if (approved) {
            booking.setStatus(Status.APPROVED);
            booking.setItem(item);
        } else
            booking.setStatus(Status.REJECTED);

        bookingRepository.save(booking);
        return BookingMapper.toBookingPrintDto(booking);
    }


    @Override
    public BookingPrintDto findById(Long userId, Long bookingId) {
        if (!userRepository.existsById(userId))
            throw new UserNotFoundException("Пользователя с таким id не существует");
        if (!bookingRepository.existsById(bookingId))
            throw new BookingNotFoundException("Брони с таким id не существует");

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(
                        "Booking с идентификатором " + bookingId + " не найден."));
        Item item = booking.getItem();
        if (!item.getOwner().getId().equals(userId) && !booking.getBooker().getId().equals(userId))
            throw new UserAccessException("Данный пользователь не может получить информацию о заданной вещи.");

        return BookingMapper.toBookingPrintDto(bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new BookingNotFoundException("Booking с идентификатором " + bookingId + " не найден.")));
    }


    @Override
    public List<BookingPrintDto> findAllByState(Long userId, String state, Integer from, Integer size) {
        if (!userRepository.existsById(userId))
            throw new UserNotFoundException("Пользователя с таким id не существует");
        Pageable pageable = FromSizeRequest.of(from, size);

        if (state.equals(ALL.getSTATE())) {
            return BookingMapper.toBookingPrintDtoList(bookingRepository.findAllByBookerIdOrderByStartDesc(userId, pageable));
        } else if (state.equals(CURRENT.getSTATE())) {
            return BookingMapper.toBookingPrintDtoList(
                    bookingRepository.findAllWithStateCurrent(userId,
                            List.of(Status.APPROVED, Status.WAITING, Status.REJECTED), pageable)
            );
        } else if (state.equals(PAST.getSTATE())) {
            return BookingMapper.toBookingPrintDtoList(
                    bookingRepository.findAllWithStatePast(userId, Status.APPROVED, pageable)
            );
        } else if (state.equals(FUTURE.getSTATE())) {
            return BookingMapper.toBookingPrintDtoList(
                    bookingRepository.findAllWithStateFuture(userId,  List.of(Status.APPROVED, Status.WAITING), pageable)
            );
        } else if (state.equals(WAITING.getSTATE())) {
            return BookingMapper.toBookingPrintDtoList(
                    bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING, pageable)
            );
        } else if (state.equals(REJECTED.getSTATE())) {
            return BookingMapper.toBookingPrintDtoList(
                    bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED, pageable)
            );
        } else
            throw new UnsupportedStateException("Unknown state: " + state);
    }


    @Override
    public List<BookingPrintDto> findAllByStateForOwner(Long userId, String state, Integer from, Integer size) {
        if (!userRepository.existsById(userId))
            throw new UserNotFoundException("Пользователя с таким id не существует");
        Pageable pageable = FromSizeRequest.of(from, size);


        if (state.equals(ALL.getSTATE())) {
            return BookingMapper.toBookingPrintDtoList(bookingRepository.findAllForOwner(userId, pageable));
        } else if (state.equals(CURRENT.getSTATE())) {
            return BookingMapper.toBookingPrintDtoList(
                    bookingRepository.findAllWithStateCurrentForOwner(userId,
                            List.of(Status.APPROVED, Status.WAITING, Status.REJECTED), pageable)
            );
        } else if (state.equals(PAST.getSTATE())) {
            return BookingMapper.toBookingPrintDtoList(
                    bookingRepository.findAllWithStatePastForOwner(userId, Status.APPROVED, pageable)
            );
        } else if (state.equals(FUTURE.getSTATE())) {
            return BookingMapper.toBookingPrintDtoList(
                    bookingRepository.findAllWithStateFutureForOwner(userId, List.of(Status.APPROVED, Status.WAITING), pageable)
            );
        } else if (state.equals(WAITING.getSTATE())) {
            return BookingMapper.toBookingPrintDtoList(
                    bookingRepository.findAllForOwnerByStatus(userId, Status.WAITING, pageable)
            );
        } else if (state.equals(REJECTED.getSTATE())) {
            return BookingMapper.toBookingPrintDtoList(
                    bookingRepository.findAllForOwnerByStatus(userId, Status.REJECTED, pageable)
            );
        } else
            throw new UnsupportedStateException("Unknown state: " + state);
    }


}
