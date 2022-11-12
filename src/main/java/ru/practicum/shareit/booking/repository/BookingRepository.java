package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enumerated.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc
            (Long bookerId, LocalDateTime now, LocalDateTime now2);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime now);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(
            Long bookerId, LocalDateTime now);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    @Query(value = "Select BOOKING.* " +
            "from BOOKING " +
            "left join ITEM I on I.ID = BOOKING.ITEM_ID " +
            "left join SHAREIT_USER SU on SU.ID = I.OWNER_ID " +
            "Where OWNER_ID =? " +
            "ORDER BY START_DATE DESC;", nativeQuery = true)
    List<Booking> findAllByOwnerId(Long ownerID);

    @Query(value = "SELECT BOOKING.* " +
            "FROM BOOKING " +
            "LEFT JOIN ITEM I on I.ID = BOOKING.ITEM_ID " +
            "LEFT JOIN SHAREIT_USER SU on SU.ID = I.OWNER_ID " +
            "WHERE OWNER_ID = ? " +
            "AND CAST(? AS date) >= CAST(START_DATE AS date) " +
            "AND CAST(? AS date) < CAST(END_DATE AS date) " +
            "ORDER BY START_DATE;", nativeQuery = true)
    List<Booking> findCurrentByOwnerId(Long ownerID, LocalDateTime now, LocalDateTime now1);

    @Query(value = "SELECT BOOKING.*\n" +
            "FROM BOOKING " +
            "LEFT JOIN ITEM I on I.ID = BOOKING.ITEM_ID " +
            "LEFT JOIN SHAREIT_USER SU on SU.ID = I.OWNER_ID " +
            "Where OWNER_ID = ? AND " +
            "CAST(END_DATE AS date) <= CAST(? AS date);", nativeQuery = true)
    List<Booking> findPastByOwnerId(Long ownerID, LocalDateTime now);

    @Query(value = "SELECT BOOKING.* " +
            "FROM BOOKING " +
            "LEFT JOIN ITEM I on I.ID = BOOKING.ITEM_ID " +
            "LEFT JOIN SHAREIT_USER SU on SU.ID = I.OWNER_ID " +
            "Where OWNER_ID = ? AND " +
            "CAST(START_DATE AS date) >= CAST(? AS date) " +
            "ORDER BY START_DATE DESC;", nativeQuery = true)
    List<Booking> findFutureByOwnerId(Long ownerID, LocalDateTime now);

    @Query(value = "SELECT BOOKING.* " +
            "FROM BOOKING " +
            "LEFT JOIN ITEM I on I.ID = BOOKING.ITEM_ID " +
            "LEFT JOIN SHAREIT_USER SU on SU.ID = I.OWNER_ID " +
            "WHERE OWNER_ID = ? AND STATUS = 'WAITING' " +
            "ORDER BY START_DATE;", nativeQuery = true)
    List<Booking> findWaitingByOwnerId(Long ownerID);

    @Query(value = "SELECT BOOKING.* " +
            "FROM BOOKING " +
            "LEFT JOIN ITEM I on I.ID = BOOKING.ITEM_ID " +
            "LEFT JOIN SHAREIT_USER SU on SU.ID = I.OWNER_ID " +
            "WHERE OWNER_ID = ? AND STATUS = 'REJECTED';", nativeQuery = true)
    List<Booking> findRejectedByOwnerId(Long ownerID);

    @Query(value = "SELECT BOOKING.* " +
            "FROM BOOKING " +
            "LEFT JOIN ITEM I on I.ID = BOOKING.ITEM_ID " +
            "LEFT JOIN SHAREIT_USER SU on SU.ID = BOOKING.BOOKER_ID " +
            "WHERE ITEM_ID = ? AND I.OWNER_ID = ? " +
            "ORDER BY START_DATE desc " +
            "LIMIT 1;", nativeQuery = true)
    Optional<Booking> findNextBookingByItemId(Long itemId, Long ownerId);

    @Query(value = "SELECT BOOKING.* " +
            "FROM BOOKING " +
            "LEFT JOIN ITEM I on I.ID = BOOKING.ITEM_ID " +
            "LEFT JOIN SHAREIT_USER SU on SU.ID = BOOKING.BOOKER_ID " +
            "WHERE ITEM_ID = ? AND I.OWNER_ID = ? " +
            "ORDER BY START_DATE " +
            "LIMIT 1;", nativeQuery = true)
    Optional<Booking> findLastBookingByItemId(Long itemId, Long ownerId);

    List<Booking> findAllByItemIdAndAndBooker_IdAndEndBefore(Long itemId, Long bookerId, LocalDateTime now);
}
