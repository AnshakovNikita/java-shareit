package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Override
    <S extends Booking> S save(S booking);

    @Query(value = "select b from Booking as b where b.booker.id = ?1 order by b.start desc")
    Page<Booking> findBookingsByBooker(Long bookerId, Pageable pageable);

    @Query(value = "select b from Booking as b where b.booker.id = ?1 and b.start > ?2 order by b.start desc")
    Page<Booking> findFutureBookingsByBooker(Long bookerId, LocalDateTime dateTime, Pageable pageable);

    @Query(value = "select b from Booking as b where b.item.id = ?1 order by b.start desc")
    Page<Booking> findBookingsByItem(Long itemId, Pageable pageable);

    @Query(value = "select b from Booking as b where b.item.id = ?1 and b.start > ?2 order by b.start desc")
    Page<Booking> findFutureBookingsByItem(Long bookerId, LocalDateTime dateTime, Pageable pageable);

    @Query(value = "select b from Booking as b where b.item.id = ?1 order by b.start asc")
    List<Booking> findBookingsByItemAsc(Long itemId);

    @Query(value = "select b from Booking as b where b.item.id = ?1 and b.status = ?2 order by b.start desc")
    Page<Booking> findBookingsByItemAndStatus(Long bookerId, BookingStatus status, Pageable pageable);

    @Query(value = "select b from Booking as b where b.booker.id = ?1 and b.status = ?2 order by b.start desc")
    Page<Booking> findBookingsByBookerAndStatus(Long bookerId, BookingStatus status, Pageable pageable);

    @Query(value = "select b from Booking as b " +
            "where b.booker.id =?1 and b.item.id = ?2 and b.status <> ?3 order by b.start desc")
    List<Booking> findBookingsByBookerAndItemAndStatusNot(Long userId, Long itemId, BookingStatus status);

    @Query(value = "select b from Booking as b WHERE b.start < ?2 and b.end > ?2 and b.booker.id = ?1 ORDER BY b.start")
    Page<Booking> findCurrentBookingForUser(Long userId, LocalDateTime time, Pageable pageable);

    @Query(value = "select b from Booking as b WHERE b.start < ?2 and b.end > ?2 and b.item.id = ?1 ORDER BY b.start")
    Page<Booking> findCurrentBookingForOwner(Long itemId, LocalDateTime time, Pageable pageable);

    @Query(value = "select b from Booking as b WHERE b.end < ?2 and b.booker.id = ?1 ORDER BY b.start")
    Page<Booking> findPastBookingForUser(Long userId, LocalDateTime time, Pageable pageable);

    @Query(value = "select b from Booking as b WHERE  b.end < ?2 and b.item.id = ?1 ORDER BY b.start")
    Page<Booking> findPastBookingForOwner(Long itemId, LocalDateTime time, Pageable pageable);

}