package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingPrintDto;

import java.util.List;

public interface BookingService {

    BookingPrintDto create(Long userId, BookingCreateDto bookingCreateDto);

    BookingPrintDto approve(Long userId, Long bookingId, Boolean approved);

    BookingPrintDto findById(Long userId, Long bookingId);

    List<BookingPrintDto> findAllByState(Long userId, String state);

    List<BookingPrintDto> findAllByStateForOwner(Long userId, String state);
}
