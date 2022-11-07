package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingPrintDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.exceptions.AlreadyExistsStatusException;
import ru.practicum.shareit.exceptions.BookingNotFoundException;
import ru.practicum.shareit.exceptions.UserAccessException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    private BookingService bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    private final User user = new User(1L, "updateName", "updateName@user.com");

    private final User owner = new User(2L, "name", "name@user.com");

    private final Item item = new Item(1L, "Аккумуляторная дрель",
            "Аккумуляторная дрель", true, owner, null);

    private final LocalDateTime start = LocalDateTime.now().plusDays(1);
    private final LocalDateTime end = LocalDateTime.now().plusDays(3);

    private final BookingPrintDto bookingPrintDto = BookingPrintDto.builder()
            .id(1L)
            .start(start)
            .end(end)
            .item(item)
            .booker(user)
            .status(Status.APPROVED)
            .build();

    private final Booking booking = Booking.builder()
            .id(1L)
            .start(start)
            .end(end)
            .item(item)
            .booker(user)
            .status(Status.APPROVED)
            .build();

    @BeforeEach
    void setUp() {
        bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository);
    }

    //    @AfterEach
//    void tearDown() {
//        verifyNoMoreInteractions(
//                userRepository
//        );
//
//    }

    @Test
    void findByIdTest() {
        final Long userId = 1L;
        final Long bookingId = 1L;

        when(userRepository.existsById(userId))
                .thenReturn(true);
        when(bookingRepository.existsById(bookingId))
                .thenReturn(true);
        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));

        final var result = bookingService.findById(userId, bookingId);

        assertThat(result.getId(), equalTo(bookingPrintDto.getId()));
        assertThat(result.getStart(), equalTo(bookingPrintDto.getStart()));
        assertThat(result.getEnd(), equalTo(bookingPrintDto.getEnd()));
        assertThat(result.getItem(), equalTo(bookingPrintDto.getItem()));
        assertThat(result.getBooker(), equalTo(bookingPrintDto.getBooker()));
        assertThat(result.getStatus(), equalTo(bookingPrintDto.getStatus()));

        verify(userRepository, times(1))
                .existsById(userId);
        verify(bookingRepository, times(1))
                .existsById(bookingId);
        verify(bookingRepository, times(2))
                .findById(bookingId);
    }

    @Test
    void findByIdUserAccessExceptionTest() {
        final User wrongUser = new User(5L, "updateName", "updateName@user.com");
        final Item wrongItem = new Item(1L, "Аккумуляторная дрель",
                "Аккумуляторная дрель", true, wrongUser, null);
        final Booking wrongBooking = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(wrongItem)
                .booker(wrongUser)
                .status(Status.APPROVED)
                .build();
        final Long userId = 3L;
        final Long bookingId = 1L;

        when(userRepository.existsById(userId))
                .thenReturn(true);
        when(bookingRepository.existsById(bookingId))
                .thenReturn(true);
        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(wrongBooking));

        final var exception = assertThrows(
                UserAccessException.class,
                () -> bookingService.findById(userId, bookingId)
        );

        assertThat("Данный пользователь не может получить информацию о заданной вещи.", equalTo(exception.getMessage()));


        verify(userRepository, times(1))
                .existsById(userId);
        verify(bookingRepository, times(1))
                .existsById(bookingId);
        verify(bookingRepository, times(1))
                .findById(bookingId);
    }

    @DisplayName("findById UserNotFoundException")
    @Test
    void existsByIdUserNotFoundExceptionTest() {
        final Long userId = 1L;
        final Long bookingId = 1L;

        when(userRepository.existsById(userId))
                .thenReturn(false);

        final var exception = assertThrows(
                UserNotFoundException.class,
                () -> bookingService.findById(userId, bookingId)
        );

        assertThat("Пользователя с таким id не существует", equalTo(exception.getMessage()));

        verify(userRepository, times(1))
                .existsById(userId);
    }

    @DisplayName("findById BookingNotFoundException")
    @Test
    void existsByIdBookingNotFoundExceptionTest() {
        final Long userId = 1L;
        final Long bookingId = 1L;

        when(userRepository.existsById(userId))
                .thenReturn(true);
        when(bookingRepository.existsById(bookingId))
                .thenReturn(false);

        final var exception = assertThrows(
                BookingNotFoundException.class,
                () -> bookingService.findById(userId, bookingId)
        );

        assertThat("Брони с таким id не существует", equalTo(exception.getMessage()));

        verify(bookingRepository, times(1))
                .existsById(bookingId);
    }


    @DisplayName("findById BookingNotFoundException")
    @Test
    void findByIdBookingNotFoundExceptionTest() {
        final Long userId = 1L;
        final Long bookingId = 1L;

        when(userRepository.existsById(userId))
                .thenReturn(true);
        when(bookingRepository.existsById(bookingId))
                .thenReturn(true);
        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.empty());

        final var exception = assertThrows(
                BookingNotFoundException.class,
                () -> bookingService.findById(userId, bookingId)
        );

        assertThat("Booking с идентификатором " + bookingId + " не найден.", equalTo(exception.getMessage()));

        verify(bookingRepository, times(1))
                .findById(bookingId);
    }

    @Test
    void approveTest() {
        final Long userId = 2L;
        final Long bookingId = 1L;
        final Boolean approved = true;
        final Booking localBooking = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(Status.REJECTED)
                .build();

        when(userRepository.existsById(userId))
                .thenReturn(true);
        when(bookingRepository.existsById(bookingId))
                .thenReturn(true);
        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(localBooking));
        when(bookingRepository.save(localBooking))
                .thenReturn(booking);

        final var result = bookingService.approve(userId, bookingId, approved);

        assertThat(result.getId(), equalTo(bookingPrintDto.getId()));
        assertThat(result.getStart(), equalTo(bookingPrintDto.getStart()));
        assertThat(result.getEnd(), equalTo(bookingPrintDto.getEnd()));
        assertThat(result.getItem(), equalTo(bookingPrintDto.getItem()));
        assertThat(result.getBooker(), equalTo(bookingPrintDto.getBooker()));
        assertThat(result.getStatus(), equalTo(bookingPrintDto.getStatus()));

        verify(userRepository, times(1))
                .existsById(userId);
        verify(bookingRepository, times(1))
                .existsById(bookingId);
        verify(bookingRepository, times(1))
                .findById(bookingId);
        verify(bookingRepository, times(1))
                .save(localBooking);
    }


    @Test
    void approveRejectedTest() {
        final Long userId = 2L;
        final Long bookingId = 1L;
        final Boolean approved = false;
        final Booking localBooking = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(Status.REJECTED)
                .build();

        when(userRepository.existsById(userId))
                .thenReturn(true);
        when(bookingRepository.existsById(bookingId))
                .thenReturn(true);
        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(localBooking));
        when(bookingRepository.save(localBooking))
                .thenReturn(booking);

        final var result = bookingService.approve(userId, bookingId, approved);

        assertThat(result.getId(), equalTo(bookingPrintDto.getId()));
        assertThat(result.getStart(), equalTo(bookingPrintDto.getStart()));
        assertThat(result.getEnd(), equalTo(bookingPrintDto.getEnd()));
        assertThat(result.getItem(), equalTo(bookingPrintDto.getItem()));
        assertThat(result.getBooker(), equalTo(bookingPrintDto.getBooker()));
        assertThat(result.getStatus(), equalTo(Status.REJECTED));

        verify(userRepository, times(1))
                .existsById(userId);
        verify(bookingRepository, times(1))
                .existsById(bookingId);
        verify(bookingRepository, times(1))
                .findById(bookingId);
        verify(bookingRepository, times(1))
                .save(localBooking);
    }

    @DisplayName("approved UserNotFoundException")
    @Test
    void approvedExistsByIdUserNotFoundExceptionTest() {
        final Long userId = 2L;
        final Long bookingId = 1L;
        final Boolean approved = true;

        when(userRepository.existsById(userId))
                .thenReturn(false);

        final var exception = assertThrows(
                UserNotFoundException.class,
                () -> bookingService.approve(userId, bookingId, approved)
        );

        assertThat("Пользователя с таким id не существует", equalTo(exception.getMessage()));

        verify(userRepository, times(1))
                .existsById(userId);
    }

    @DisplayName("approved BookingNotFoundException")
    @Test
    void approvedExistsByIdBookingNotFoundExceptionTest() {
        final Long userId = 2L;
        final Long bookingId = 1L;
        final Boolean approved = true;

        when(userRepository.existsById(userId))
                .thenReturn(true);
        when(bookingRepository.existsById(bookingId))
                .thenReturn(false);

        final var exception = assertThrows(
                BookingNotFoundException.class,
                () -> bookingService.approve(userId, bookingId, approved)
        );

        assertThat("Брони с таким id не существует", equalTo(exception.getMessage()));

        verify(userRepository, times(1))
                .existsById(userId);
        verify(bookingRepository, times(1))
                .existsById(bookingId);
    }

    @DisplayName("approved BookingNotFoundException")
    @Test
    void approvedFindByIdBookingNotFoundExceptionTest() {
        final Long userId = 2L;
        final Long bookingId = 1L;
        final Boolean approved = true;

        when(userRepository.existsById(userId))
                .thenReturn(true);
        when(bookingRepository.existsById(bookingId))
                .thenReturn(true);
        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.empty());

        final var exception = assertThrows(
                BookingNotFoundException.class,
                () -> bookingService.approve(userId, bookingId, approved)
        );

        assertThat("Booking с идентификатором " + bookingId + " не найден.", equalTo(exception.getMessage()));

        verify(userRepository, times(1))
                .existsById(userId);
        verify(bookingRepository, times(1))
                .existsById(bookingId);
        verify(bookingRepository, times(1))
                .findById(bookingId);
    }

    @Test
    void approvedUserAccessExceptionTest() {
        final Long userId = 1L;
        final Long bookingId = 1L;
        final Boolean approved = true;

        when(userRepository.existsById(userId))
                .thenReturn(true);
        when(bookingRepository.existsById(bookingId))
                .thenReturn(true);
        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));

        final var exception = assertThrows(
                UserAccessException.class,
                () -> bookingService.approve(userId, bookingId, approved)
        );

        assertThat("Данный пользователь не может управлять запрашиваемой бронью.", equalTo(exception.getMessage()));


        verify(userRepository, times(1))
                .existsById(userId);
        verify(bookingRepository, times(1))
                .existsById(bookingId);
        verify(bookingRepository, times(1))
                .findById(bookingId);
    }

    @Test
    void approvedAlreadyExistsStatusException() {
        final Long userId = 2L;
        final Long bookingId = 1L;
        final Boolean approved = true;

        when(userRepository.existsById(userId))
                .thenReturn(true);
        when(bookingRepository.existsById(bookingId))
                .thenReturn(true);
        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));

        final var exception = assertThrows(
                AlreadyExistsStatusException.class,
                () -> bookingService.approve(userId, bookingId, approved)
        );

        assertThat("Статус уже подтвержден", equalTo(exception.getMessage()));


        verify(userRepository, times(1))
                .existsById(userId);
        verify(bookingRepository, times(1))
                .existsById(bookingId);
        verify(bookingRepository, times(1))
                .findById(bookingId);
    }

    @Test
    void bookingShortInfoDto() {
        User owner = User.builder()
                .id(2L)
                .name("John")
                .email("some@email.com")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("Аккумуляторная дрель")
                .description("Аккумуляторная дрель")
                .available(true)
                .owner(owner)
                .requestId(null)
                .build();
        User user = User.builder()
                .id(1L)
                .name("updateName")
                .email("updateName@user.com")
                .build();
        final Booking booking = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .build();
        final var result = BookingMapper.toBookingShortInfoDto(booking);

        assertThat(result.getId(), equalTo(booking.getId()));
        assertThat(result.getStart(), equalTo(booking.getStart()));
        assertThat(result.getEnd(), equalTo(booking.getEnd()));
        assertThat(result.getItemId(), equalTo(booking.getItem().getId()));
        assertThat(result.getBookerId(), equalTo(booking.getBooker().getId()));
        assertThat(result.getStatus(), equalTo(booking.getStatus()));
    }
}