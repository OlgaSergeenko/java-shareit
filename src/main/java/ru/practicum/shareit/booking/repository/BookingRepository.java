package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enumerated.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            Long bookerId, LocalDateTime now, LocalDateTime now2);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime now);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(
            Long bookerId, LocalDateTime now);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    List<Booking> findAllByItem_OwnerIdOrderByStartDesc(Long ownerID);

    List<Booking> findAllByItem_OwnerIdAndStartLessThanEqualAndEndGreaterThanOrderByStart(
            Long ownerID, LocalDateTime now, LocalDateTime now1);

    List<Booking> findAllByItem_OwnerIdAndEndLessThanEqual(Long ownerID, LocalDateTime now);

    List<Booking> findAllByItem_OwnerIdAndStartGreaterThanEqualOrderByStartDesc(Long ownerID, LocalDateTime now);

    List<Booking> findAllByItem_OwnerIdAndStatusOrderByStart(Long ownerID, BookingStatus status);

    List<Booking> findAllByItem_OwnerIdAndStatus(Long ownerID, BookingStatus status);

    Optional<Booking> findFirstByItem_Owner_IdAndAndItem_IdOrderByStart(Long ownerId, Long itemId);

    Optional<Booking> findFirstByItem_OwnerIdAndIdOrderByStartDesc(Long ownerId, Long itemId);

    List<Booking> findAllByItemIdAndAndBooker_IdAndEndBefore(Long itemId, Long bookerId, LocalDateTime now);
}
