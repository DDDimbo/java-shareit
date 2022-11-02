package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.Status;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {


    @Override
    <S extends Booking> S save(S booking);

    @Override
    Optional<Booking> findById(Long id);

    @Override
    boolean existsById(Long id);

    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId, Pageable pageable);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long userId, Status status, Pageable pageable);


    @Query(value = "SELECT b " +
            "FROM Booking AS b " +
            "LEFT OUTER JOIN Item as i ON b.item.id=i.id " +
            "WHERE i.owner.id = ?1 AND b.status = ?2 " +
            "ORDER BY b.start DESC ")
    List<Booking> findAllForOwnerByStatus(Long userId, Status status, Pageable pageable);


    @Query(value = "SELECT b " +
            "FROM Booking AS b " +
            "LEFT OUTER JOIN Item as i ON b.item.id=i.id " +
            "WHERE i.owner.id = ?1 " +
            "ORDER BY b.start DESC ")
    List<Booking> findAllForOwner(Long userId, Pageable pageable);

//    List<Booking> findAllByItemIdAndStatusIs(Long itemId, Status status);

    @Query("select b from Booking b " +
            "where b.booker.id = ?1 and b.status in ?2 " +
            "and b.start <= current_timestamp and b.end > current_timestamp " +
            "order by b.start desc ")
    List<Booking> findAllWithStateCurrent(Long userId, List<Status> status, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.item.owner.id = ?1 and b.status in ?2 " +
            "and b.start <= current_timestamp and b.end > current_timestamp " +
            "order by b.start desc ")
    List<Booking> findAllWithStateCurrentForOwner(Long userId, List<Status> status, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.booker.id = ?1 " +
            "and b.status in ?2 and b.start > current_timestamp " +
            "order by b.start desc ")
    List<Booking> findAllWithStateFuture(Long userId, List<Status> status, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.item.owner.id = ?1 " +
            "and b.status in ?2 and b.start > current_timestamp " +
            "order by b.start desc ")
    List<Booking> findAllWithStateFutureForOwner(Long userId, List<Status> status, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.booker.id = ?1 and b.status = ?2 " +
            "and b.end < current_timestamp " +
            "order by b.start desc ")
    List<Booking> findAllWithStatePast(Long userId, Status status, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.item.owner.id = ?1 and b.status = ?2 " +
            "and b.end < current_timestamp " +
            "order by b.start desc ")
    List<Booking> findAllWithStatePastForOwner(Long userId, Status status, Pageable pageable);

    @Query("select count(b) > 0 " +
            "from Booking b " +
            "where b.booker.id = ?1 and b.item.id = ?2 and b.status in ?3 and b.end < current_timestamp ")
    Boolean existsApprovedBookingByBookerAndItemBeforeNow(Long userId, Long itemId, List<Status> status);

    @Query("select b from Booking b " +
            "where b.item.id = ?1 " +
            "and b.status = 'APPROVED'" +
            "and b.start > current_timestamp " +
            "order by b.start asc ")
    Optional<Booking> findFirstNextBooking(Long itemId);

    @Query("select b from Booking b " +
            "where b.item.id = ?1 " +
            "and b.status = 'APPROVED'" +
            "and b.end < current_timestamp " +
            "order by b.end desc ")
    Optional<Booking> findFirstLastBooking(Long itemId);
}
