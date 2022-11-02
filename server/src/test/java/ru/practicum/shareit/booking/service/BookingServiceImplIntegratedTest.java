package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
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

import javax.persistence.EntityManager;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImplIntegratedTest {

    private final EntityManager em;

    private final BookingRepository bookingRepository;

    private final BookingService bookingService;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    private Booking booking;

    private User user;

    private User owner;

    private Item item;

    @BeforeEach
    void beforeEach() {
        user = User.builder()
                .name("updateName")
                .email("updateName@user.com")
                .build();
        userRepository.save(user);
        owner = User.builder()
                .name("someName")
                .email("some@user.com")
                .build();
        userRepository.save(owner);
        item = Item.builder()
                .name("Аккумуляторная дрель")
                .description("Аккумуляторная дрель")
                .available(true)
                .owner(owner)
                .build();
        itemRepository.save(item);
    }

    @Test
    void createTest() {
        final LocalDateTime start = LocalDateTime.now().plusDays(1);
        final LocalDateTime end = LocalDateTime.now().plusDays(3);
        final BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .start(start)
                .end(end)
                .itemId(item.getId())
                .build();
        final BookingPrintDto bookingPrintDto = BookingPrintDto.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(Status.WAITING)
                .build();

        final var result = bookingService.create(user.getId(), bookingCreateDto);

        assertNotNull(result.getId());
        assertThat(result.getBooker(), equalTo(bookingPrintDto.getBooker()));
        assertThat(result.getItem(), equalTo(bookingPrintDto.getItem()));
        assertThat(result.getStart(), equalTo(bookingPrintDto.getStart()));
        assertThat(result.getEnd(), equalTo(bookingPrintDto.getEnd()));
        assertThat(result.getStatus(), equalTo(bookingPrintDto.getStatus()));
    }

    @Test
    void createUserNotFoundExceptionTest() {
        final LocalDateTime start = LocalDateTime.now().plusDays(1);
        final LocalDateTime end = LocalDateTime.now().plusDays(3);
        final BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .start(start)
                .end(end)
                .itemId(item.getId())
                .build();

        final var exception = assertThrows(
                UserNotFoundException.class,
                () -> bookingService.create(999L, bookingCreateDto)
        );

        assertThat("Пользователя с таким id не существует", equalTo(exception.getMessage()));
    }

    @Test
    void createDateTimeExceptionTest() {
        final LocalDateTime start = LocalDateTime.now().plusDays(3);
        final LocalDateTime end = LocalDateTime.now().plusDays(1);
        final BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .start(start)
                .end(end)
                .itemId(item.getId())
                .build();

        final var exception = assertThrows(
                DateTimeException.class,
                () -> bookingService.create(user.getId(), bookingCreateDto)
        );

        assertThat("StartTime не может быть после EndTime или равняться ему", equalTo(exception.getMessage()));
    }

    @Test
    void createBookingAccessExceptionTest() {
        final LocalDateTime start = LocalDateTime.now().plusDays(1);
        final LocalDateTime end = LocalDateTime.now().plusDays(3);
        final BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .start(start)
                .end(end)
                .itemId(item.getId())
                .build();

        final var exception = assertThrows(
                BookingAccessException.class,
                () -> bookingService.create(owner.getId(), bookingCreateDto)
        );

        assertThat("Владелец не может создать бронь на свою вещь", equalTo(exception.getMessage()));
    }

    @Test
    void createAccessErrorForItemExceptionTest() {
        final LocalDateTime start = LocalDateTime.now().plusDays(1);
        final LocalDateTime end = LocalDateTime.now().plusDays(3);
        Item newItem = Item.builder()
                .name("Аккумуляторная дрель")
                .description("Аккумуляторная дрель")
                .available(false)
                .owner(owner)
                .build();
        itemRepository.save(newItem);
        final BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .start(start)
                .end(end)
                .itemId(newItem.getId())
                .build();

        final var exception = assertThrows(
                AccessErrorForItemException.class,
                () -> bookingService.create(user.getId(), bookingCreateDto)
        );

        assertThat("Вещь с указанным id недоступна для запроса на бронирование.", equalTo(exception.getMessage()));

    }

    @Test
    void findAllByStateAllTest() {
        final Long userId = user.getId();
        final int from = 0;
        final int size = 10;
        final String state = "ALL";
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(3);
        booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking);
        final BookingPrintDto bookingPrintDto = BookingPrintDto.builder()
                .id(booking.getId())
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .build();

        final var result = bookingService.findAllByState(userId, state, from, size);

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(bookingPrintDto.getId()));
        assertThat(result.get(0).getStart(), equalTo(bookingPrintDto.getStart()));
        assertThat(result.get(0).getEnd(), equalTo(bookingPrintDto.getEnd()));
        assertThat(result.get(0).getItem(), equalTo(bookingPrintDto.getItem()));
        assertThat(result.get(0).getBooker(), equalTo(bookingPrintDto.getBooker()));
        assertThat(result.get(0).getStatus(), equalTo(bookingPrintDto.getStatus()));
    }

    @Test
    void findAllByStateRejectedTest() {
        final Long userId = user.getId();
        final int from = 0;
        final int size = 10;
        final String state = "REJECTED";
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(3);
        booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(Status.REJECTED)
                .build();
        bookingRepository.save(booking);
        final BookingPrintDto bookingPrintDto = BookingPrintDto.builder()
                .id(booking.getId())
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(Status.REJECTED)
                .build();

        final var result = bookingService.findAllByState(userId, state, from, size);

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(bookingPrintDto.getId()));
        assertThat(result.get(0).getStart(), equalTo(bookingPrintDto.getStart()));
        assertThat(result.get(0).getEnd(), equalTo(bookingPrintDto.getEnd()));
        assertThat(result.get(0).getItem(), equalTo(bookingPrintDto.getItem()));
        assertThat(result.get(0).getBooker(), equalTo(bookingPrintDto.getBooker()));
        assertThat(result.get(0).getStatus(), equalTo(bookingPrintDto.getStatus()));
    }

    @Test
    void findAllByStateFutureTest() {
        final Long userId = user.getId();
        final int from = 0;
        final int size = 10;
        final String state = "FUTURE";
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(3);
        booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking);
        final BookingPrintDto bookingPrintDto = BookingPrintDto.builder()
                .id(booking.getId())
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .build();

        final var result = bookingService.findAllByState(userId, state, from, size);

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(bookingPrintDto.getId()));
        assertThat(result.get(0).getStart(), equalTo(bookingPrintDto.getStart()));
        assertThat(result.get(0).getEnd(), equalTo(bookingPrintDto.getEnd()));
        assertThat(result.get(0).getItem(), equalTo(bookingPrintDto.getItem()));
        assertThat(result.get(0).getBooker(), equalTo(bookingPrintDto.getBooker()));
        assertThat(result.get(0).getStatus(), equalTo(bookingPrintDto.getStatus()));
    }


    @Test
    void findAllByStateWaitingTest() {
        final Long userId = user.getId();
        final int from = 0;
        final int size = 10;
        final String state = "WAITING";
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(3);
        booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(Status.WAITING)
                .build();
        bookingRepository.save(booking);
        final BookingPrintDto bookingPrintDto = BookingPrintDto.builder()
                .id(booking.getId())
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(Status.WAITING)
                .build();

        final var result = bookingService.findAllByState(userId, state, from, size);

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(bookingPrintDto.getId()));
        assertThat(result.get(0).getStart(), equalTo(bookingPrintDto.getStart()));
        assertThat(result.get(0).getEnd(), equalTo(bookingPrintDto.getEnd()));
        assertThat(result.get(0).getItem(), equalTo(bookingPrintDto.getItem()));
        assertThat(result.get(0).getBooker(), equalTo(bookingPrintDto.getBooker()));
        assertThat(result.get(0).getStatus(), equalTo(bookingPrintDto.getStatus()));
    }

    @Test
    void findAllByStateCurrentTest() {
        final Long userId = user.getId();
        final int from = 0;
        final int size = 10;
        final String state = "CURRENT";
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(3);
        booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking);
        final BookingPrintDto bookingPrintDto = BookingPrintDto.builder()
                .id(booking.getId())
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .build();

        final var result = bookingService.findAllByState(userId, state, from, size);

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(bookingPrintDto.getId()));
        assertThat(result.get(0).getStart(), equalTo(bookingPrintDto.getStart()));
        assertThat(result.get(0).getEnd(), equalTo(bookingPrintDto.getEnd()));
        assertThat(result.get(0).getItem(), equalTo(bookingPrintDto.getItem()));
        assertThat(result.get(0).getBooker(), equalTo(bookingPrintDto.getBooker()));
        assertThat(result.get(0).getStatus(), equalTo(bookingPrintDto.getStatus()));
    }

    @Test
    void findAllByStatePastTest() {
        final Long userId = user.getId();
        final int from = 0;
        final int size = 10;
        final String state = "PAST";
        LocalDateTime start = LocalDateTime.now().minusDays(3);
        LocalDateTime end = LocalDateTime.now().minusDays(1);
        booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking);
        final BookingPrintDto bookingPrintDto = BookingPrintDto.builder()
                .id(booking.getId())
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .build();

        final var result = bookingService.findAllByState(userId, state, from, size);

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(bookingPrintDto.getId()));
        assertThat(result.get(0).getStart(), equalTo(bookingPrintDto.getStart()));
        assertThat(result.get(0).getEnd(), equalTo(bookingPrintDto.getEnd()));
        assertThat(result.get(0).getItem(), equalTo(bookingPrintDto.getItem()));
        assertThat(result.get(0).getBooker(), equalTo(bookingPrintDto.getBooker()));
        assertThat(result.get(0).getStatus(), equalTo(bookingPrintDto.getStatus()));
    }

    @Test
    void findAllByStateUserNotFoundExceptionTest() {
        final int from = 0;
        final int size = 10;
        final String state = "ALL";
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(3);
        booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking);

        final var exception = assertThrows(
                UserNotFoundException.class,
                () -> bookingService.findAllByState(9999L, state, from, size));

        assertThat("Пользователя с таким id не существует", equalTo(exception.getMessage()));

    }

    @Test
    void findAllByStateUnsupportedStateExceptionTest() {
        final Long userId = user.getId();
        final int from = 0;
        final int size = 10;
        final String state = "INCORRECT";
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(3);
        booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking);

        final var exception = assertThrows(
                UnsupportedStateException.class,
                () -> bookingService.findAllByState(userId, state, from, size));

        assertThat("Unknown state: " + state, equalTo(exception.getMessage()));
    }

    @Test
    void findAllByStateForOwnerAllTest() {
        final Long ownerId = owner.getId();
        final int from = 0;
        final int size = 10;
        final String state = "ALL";
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(3);
        booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking);
        final BookingPrintDto bookingPrintDto = BookingPrintDto.builder()
                .id(booking.getId())
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .build();

        final var result = bookingService.findAllByStateForOwner(ownerId, state, from, size);

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(bookingPrintDto.getId()));
        assertThat(result.get(0).getStart(), equalTo(bookingPrintDto.getStart()));
        assertThat(result.get(0).getEnd(), equalTo(bookingPrintDto.getEnd()));
        assertThat(result.get(0).getItem(), equalTo(bookingPrintDto.getItem()));
        assertThat(result.get(0).getBooker(), equalTo(bookingPrintDto.getBooker()));
        assertThat(result.get(0).getStatus(), equalTo(bookingPrintDto.getStatus()));
    }

    @Test
    void findAllByStateForOwnerRejectedTest() {
        final Long ownerId = owner.getId();
        final int from = 0;
        final int size = 10;
        final String state = "REJECTED";
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(3);
        booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(Status.REJECTED)
                .build();
        bookingRepository.save(booking);
        final BookingPrintDto bookingPrintDto = BookingPrintDto.builder()
                .id(booking.getId())
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(Status.REJECTED)
                .build();

        final var result = bookingService.findAllByStateForOwner(ownerId, state, from, size);

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(bookingPrintDto.getId()));
        assertThat(result.get(0).getStart(), equalTo(bookingPrintDto.getStart()));
        assertThat(result.get(0).getEnd(), equalTo(bookingPrintDto.getEnd()));
        assertThat(result.get(0).getItem(), equalTo(bookingPrintDto.getItem()));
        assertThat(result.get(0).getBooker(), equalTo(bookingPrintDto.getBooker()));
        assertThat(result.get(0).getStatus(), equalTo(bookingPrintDto.getStatus()));
    }

    @Test
    void findAllByStateForOwnerFutureTest() {
        final Long ownerId = owner.getId();
        final int from = 0;
        final int size = 10;
        final String state = "FUTURE";
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(3);
        booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking);
        final BookingPrintDto bookingPrintDto = BookingPrintDto.builder()
                .id(booking.getId())
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .build();

        final var result = bookingService.findAllByStateForOwner(ownerId, state, from, size);

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(bookingPrintDto.getId()));
        assertThat(result.get(0).getStart(), equalTo(bookingPrintDto.getStart()));
        assertThat(result.get(0).getEnd(), equalTo(bookingPrintDto.getEnd()));
        assertThat(result.get(0).getItem(), equalTo(bookingPrintDto.getItem()));
        assertThat(result.get(0).getBooker(), equalTo(bookingPrintDto.getBooker()));
        assertThat(result.get(0).getStatus(), equalTo(bookingPrintDto.getStatus()));
    }


    @Test
    void findAllByStateWaitingForOwnerTest() {
        final Long ownerId = owner.getId();
        final int from = 0;
        final int size = 10;
        final String state = "WAITING";
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(3);
        booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(Status.WAITING)
                .build();
        bookingRepository.save(booking);
        final BookingPrintDto bookingPrintDto = BookingPrintDto.builder()
                .id(booking.getId())
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(Status.WAITING)
                .build();

        final var result = bookingService.findAllByStateForOwner(ownerId, state, from, size);

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(bookingPrintDto.getId()));
        assertThat(result.get(0).getStart(), equalTo(bookingPrintDto.getStart()));
        assertThat(result.get(0).getEnd(), equalTo(bookingPrintDto.getEnd()));
        assertThat(result.get(0).getItem(), equalTo(bookingPrintDto.getItem()));
        assertThat(result.get(0).getBooker(), equalTo(bookingPrintDto.getBooker()));
        assertThat(result.get(0).getStatus(), equalTo(bookingPrintDto.getStatus()));
    }

    @Test
    void findAllByStateCurrentForOwnerTest() {
        final Long ownerId = owner.getId();
        final int from = 0;
        final int size = 10;
        final String state = "CURRENT";
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(3);
        booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking);
        final BookingPrintDto bookingPrintDto = BookingPrintDto.builder()
                .id(booking.getId())
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .build();

        final var result = bookingService.findAllByStateForOwner(ownerId, state, from, size);

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(bookingPrintDto.getId()));
        assertThat(result.get(0).getStart(), equalTo(bookingPrintDto.getStart()));
        assertThat(result.get(0).getEnd(), equalTo(bookingPrintDto.getEnd()));
        assertThat(result.get(0).getItem(), equalTo(bookingPrintDto.getItem()));
        assertThat(result.get(0).getBooker(), equalTo(bookingPrintDto.getBooker()));
        assertThat(result.get(0).getStatus(), equalTo(bookingPrintDto.getStatus()));
    }

    @Test
    void findAllByStatePastForOwnerTest() {
        final Long ownerId = owner.getId();
        final int from = 0;
        final int size = 10;
        final String state = "PAST";
        LocalDateTime start = LocalDateTime.now().minusDays(3);
        LocalDateTime end = LocalDateTime.now().minusDays(1);
        booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking);
        final BookingPrintDto bookingPrintDto = BookingPrintDto.builder()
                .id(booking.getId())
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .build();

        final var result = bookingService.findAllByStateForOwner(ownerId, state, from, size);

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(bookingPrintDto.getId()));
        assertThat(result.get(0).getStart(), equalTo(bookingPrintDto.getStart()));
        assertThat(result.get(0).getEnd(), equalTo(bookingPrintDto.getEnd()));
        assertThat(result.get(0).getItem(), equalTo(bookingPrintDto.getItem()));
        assertThat(result.get(0).getBooker(), equalTo(bookingPrintDto.getBooker()));
        assertThat(result.get(0).getStatus(), equalTo(bookingPrintDto.getStatus()));
    }


    @Test
    void findAllByStateForOwnerUserNotFoundExceptionTest() {
        final int from = 0;
        final int size = 10;
        final String state = "ALL";
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(3);
        booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking);

        final var exception = assertThrows(
                UserNotFoundException.class,
                () -> bookingService.findAllByStateForOwner(9999L, state, from, size));

        assertThat("Пользователя с таким id не существует", equalTo(exception.getMessage()));
    }

    @Test
    void findAllByStateForOwnerUnsupportedStateExceptionTest() {
        final Long userId = user.getId();
        final int from = 0;
        final int size = 10;
        final String state = "INCORRECT";
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(3);
        booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking);

        final var exception = assertThrows(
                UnsupportedStateException.class,
                () -> bookingService.findAllByStateForOwner(userId, state, from, size));

        assertThat("Unknown state: " + state, equalTo(exception.getMessage()));
    }

}
