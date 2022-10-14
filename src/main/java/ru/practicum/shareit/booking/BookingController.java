package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingPrintDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.markerinterface.Create;

import java.util.Collection;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingPrintDto create(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                  @Validated(Create.class) @RequestBody BookingCreateDto bookingCreateDtoDto
    ) {
        log.info("Create booking by user with id={}", userId);
        return bookingService.create(userId, bookingCreateDtoDto);
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingPrintDto approve(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                   @PathVariable Long bookingId,
                                   @RequestParam(value = "approved") Boolean approved) {
        log.info("Update booking by user with id={}", userId);
        return bookingService.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingPrintDto findById(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                    @PathVariable Long bookingId) {
        log.info("Get booking by id with userId={}", userId);
        return bookingService.findById(userId, bookingId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<BookingPrintDto> findAllByState(
            @RequestHeader(value = "X-Sharer-User-Id") Long userId,
            @RequestParam(value = "state", required = false, defaultValue = "ALL") String state
    ) {
        log.info("Get booking by value: {}", userId);
        return bookingService.findAllByState(userId, state);
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public Collection<BookingPrintDto> findAllByParamForOwner(
            @RequestHeader(value = "X-Sharer-User-Id") Long userId,
            @RequestParam(value = "state", required = false, defaultValue = "ALL") String state
    ) {
        log.info("Get booking by value: {}", userId);
        return bookingService.findAllByStateForOwner(userId, state);
    }
}
