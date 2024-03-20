package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.enums.State;
import ru.practicum.shareit.exceptions.UnsupportedStateException;
import ru.practicum.shareit.markerinterface.Create;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
	private final BookingClient bookingClient;

	@PostMapping
	public ResponseEntity<Object> create(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
								  @Validated(Create.class) @RequestBody BookingCreateDto bookingCreateDto
	) {
		log.info("Creating booking {}, userId={} validation", bookingCreateDto, userId);
		return bookingClient.bookItem(userId, bookingCreateDto);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> approve(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
								   @PathVariable Long bookingId,
								   @RequestParam(value = "approved") Boolean approved) {
		log.info("Update booking by user with id={} validation", userId);
		return bookingClient.approveBooking(userId, bookingId, approved);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> findById(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
									@PathVariable Long bookingId) {
		log.info("Get booking {}, userId={} validation", bookingId, userId);
		return bookingClient.getBooking(userId, bookingId);
	}

	@GetMapping
	public ResponseEntity<Object> findAllByState(
			@PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
			@Positive @RequestParam(value = "size", defaultValue = "10") Integer size,
			@RequestHeader(value = "X-Sharer-User-Id") Long userId,
			@RequestParam(value = "state", required = false, defaultValue = "ALL") String stateParam
	) {
		State state = State.from(stateParam)
				.orElseThrow(() -> new UnsupportedStateException("Unknown state: " + stateParam));
		log.info("Get booking with state {}, userId={}, from={}, size={} validation", state, userId, from, size);
		return bookingClient.getBookings(userId, state, from, size);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> findAllByParamForOwner(
			@PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
			@Positive @RequestParam(value = "size", defaultValue = "10") Integer size,
			@RequestHeader(value = "X-Sharer-User-Id") Long userId,
			@RequestParam(value = "state", required = false, defaultValue = "ALL") String stateParam
	) {
		State state = State.from(stateParam)
				.orElseThrow(() -> new UnsupportedStateException("Unknown state: " + stateParam));
		log.info("Get booking by value: {} validation", userId);
		return bookingClient.getBookingsForOwner(userId, state, from, size);
	}
}
