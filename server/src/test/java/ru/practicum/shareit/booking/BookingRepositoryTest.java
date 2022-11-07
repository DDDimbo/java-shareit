package ru.practicum.shareit.booking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.utility.FromSizeRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class BookingRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    public void contextLoads() {
        assertNotNull(em);
    }

    private User user;

    private User owner;

    private Item item;

    private final Pageable pageable = FromSizeRequest.of(0, 10);


    @BeforeEach
    public void beforeEach() {
        user = User.builder()
                .name("updateName")
                .email("updateName@user.com")
                .build();
        em.persist(user);
        owner = User.builder()
                .name("someName")
                .email("some@user.com")
                .build();
        em.persist(owner);
        item = Item.builder()
                .name("Аккумуляторная дрель")
                .description("Аккумуляторная дрель")
                .available(true)
                .owner(owner)
                .build();
        em.persist(item);
    }

    @AfterEach
    public void afterEach() {
        em.clear();
    }

    @Test
    public void findAllByBookerIdOrderByStartDescTest() {
        final LocalDateTime start = LocalDateTime.now().plusDays(1);
        final LocalDateTime end = LocalDateTime.now().plusDays(3);
        final Booking booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .build();
        em.persist(booking);

        final var result = bookingRepository.findAllByBookerIdOrderByStartDesc(user.getId(), pageable);

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(booking.getId()));
    }

    @Test
    public void findAllForOwnerTest() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(3);
        final Booking booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .build();
        em.persist(booking);

        final var result = bookingRepository.findAllForOwner(owner.getId(), pageable);

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(booking.getId()));
    }

    @Test
    public void findAllWithStateCurrentTest() {
        final LocalDateTime start = LocalDateTime.now().minusDays(1);
        final LocalDateTime end = LocalDateTime.now().plusDays(3);
        final Booking booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .build();
        em.persist(booking);
        final List<Status> status = List.of(Status.APPROVED, Status.WAITING);

        final var result = bookingRepository.findAllWithStateCurrent(user.getId(), status, pageable);

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(booking.getId()));
    }

    @Test
    public void findAllWithStateCurrentForOwnerTest() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(3);
        final Booking booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .build();
        em.persist(booking);
        final List<Status> status = List.of(Status.APPROVED, Status.WAITING);

        final var result = bookingRepository.findAllWithStateCurrentForOwner(owner.getId(), status, pageable);

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(booking.getId()));
    }


    @Test
    public void findAllWithStatePastTest() {
        final LocalDateTime start = LocalDateTime.now().minusDays(3);
        final LocalDateTime end = LocalDateTime.now().minusDays(1);
        final Booking booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .build();
        em.persist(booking);

        final var result = bookingRepository.findAllWithStatePast(user.getId(), Status.APPROVED, pageable);

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(booking.getId()));
    }

    @Test
    public void findAllWithStatePastForOwnerTest() {
        LocalDateTime start = LocalDateTime.now().minusDays(3);
        LocalDateTime end = LocalDateTime.now().minusDays(1);
        final Booking booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .build();
        em.persist(booking);

        final var result = bookingRepository.findAllWithStatePastForOwner(owner.getId(), Status.APPROVED, pageable);

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(booking.getId()));
    }


    @Test
    public void findAllWithStateFutureTest() {
        final LocalDateTime start = LocalDateTime.now().plusDays(3);
        final LocalDateTime end = LocalDateTime.now().plusDays(5);
        final Booking booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .build();
        em.persist(booking);
        final List<Status> status = List.of(Status.APPROVED, Status.WAITING);

        final var result = bookingRepository.findAllWithStateFuture(user.getId(), status, pageable);

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(booking.getId()));
    }

    @Test
    public void findAllWithStateFutureForOwnerTest() {
        LocalDateTime start = LocalDateTime.now().plusDays(3);
        LocalDateTime end = LocalDateTime.now().plusDays(5);
        final Booking booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .build();
        em.persist(booking);
        final List<Status> status = List.of(Status.APPROVED, Status.WAITING);

        final var result = bookingRepository.findAllWithStateFutureForOwner(owner.getId(), status, pageable);

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(booking.getId()));
    }

    @Test
    public void findAllByBookerIdAndStatusOrderByStartDescTest() {
        LocalDateTime start = LocalDateTime.now().plusDays(3);
        LocalDateTime end = LocalDateTime.now().plusDays(5);
        final Booking booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(Status.WAITING)
                .build();
        em.persist(booking);

        final var result = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(user.getId(), Status.WAITING, pageable);

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(booking.getId()));
    }

    @Test
    public void findAllForOwnerByStatusWaitingTest() {
        LocalDateTime start = LocalDateTime.now().plusDays(3);
        LocalDateTime end = LocalDateTime.now().plusDays(5);
        final Booking booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(Status.WAITING)
                .build();
        em.persist(booking);

        final var result = bookingRepository.findAllForOwnerByStatus(owner.getId(), Status.WAITING, pageable);

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(booking.getId()));
    }

    @Test
    public void findAllByBookerIdAndStatusOrderByStartDescRejectedTest() {
        LocalDateTime start = LocalDateTime.now().plusDays(3);
        LocalDateTime end = LocalDateTime.now().plusDays(5);
        final Booking booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(Status.REJECTED)
                .build();
        em.persist(booking);

        final var result = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(user.getId(), Status.REJECTED, pageable);

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(booking.getId()));
    }

    @Test
    public void findAllForOwnerByStatusRejectedTest() {
        LocalDateTime start = LocalDateTime.now().plusDays(3);
        LocalDateTime end = LocalDateTime.now().plusDays(5);
        final Booking booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(Status.REJECTED)
                .build();
        em.persist(booking);

        final var result = bookingRepository.findAllForOwnerByStatus(owner.getId(), Status.REJECTED, pageable);

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(booking.getId()));
    }

    @Test
    public void findFirstLastBooking() {
        final Booking lastBooking = Booking.builder()
                .start(LocalDateTime.now().minusDays(5))
                .end(LocalDateTime.now().minusDays(3))
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .build();
        em.persist(lastBooking);
        final Booking nextBooking = Booking.builder()
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(5))
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .build();
        em.persist(nextBooking);
        final Booking currentBooking = Booking.builder()
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .build();
        em.persist(currentBooking);

        final var result = bookingRepository.findFirstLastBooking(item.getId()).get();

        assertThat(result.getId(), equalTo(lastBooking.getId()));
    }

    @Test
    public void findFirstNextBooking() {
        final Booking lastBooking = Booking.builder()
                .start(LocalDateTime.now().minusDays(5))
                .end(LocalDateTime.now().minusDays(3))
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .build();
        em.persist(lastBooking);
        final Booking nextBooking = Booking.builder()
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(5))
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .build();
        em.persist(nextBooking);
        final Booking currentBooking = Booking.builder()
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .build();
        em.persist(currentBooking);

        final var result = bookingRepository.findFirstNextBooking(item.getId()).get();

        assertThat(result.getId(), equalTo(nextBooking.getId()));
    }

    @Test
    public void existsApprovedBookingByBookerAndItemBeforeNow() {
        LocalDateTime start = LocalDateTime.now().minusDays(3);
        LocalDateTime end = LocalDateTime.now().minusDays(1);
        final Booking booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .build();
        em.persist(booking);
        final List<Status> status = List.of(Status.APPROVED, Status.CANCELED);

        final var result = bookingRepository.existsApprovedBookingByBookerAndItemBeforeNow(user.getId(), item.getId(), status);

        assertTrue(result);
    }
}
