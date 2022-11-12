package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.enumerated.BookingState;
import ru.practicum.shareit.enumerated.BookingStatus;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.UnavailableBookingException;
import ru.practicum.shareit.exception.UnsupportedStatusException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserShortDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserServiceImpl userService;
    private final ItemService itemService;

    @Override
    public BookingDto create(BookingDto bookingDto, Long userId) {
        UserShortDto userShort = new UserShortDto();
        userShort.setId(userId);
        bookingDto.setBooker(userShort);
        Booking booking = BookingMapper.toBooking(bookingDto);

        checkDates(booking.getStart(), booking.getEnd());
        Item item = itemService.getItemById(bookingDto.getItemId());
        if (Objects.equals(item.getOwner().getId(), userId)) {
            throw new BookingNotFoundException("Owner cannot book his own item");
        }
        if (!item.getIsAvailable()) {
            throw new UnavailableBookingException("Booking is not available");
        }
        User user = UserMapper.toUser(userService.findById(userId));
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        return BookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto setBookingStatus(Long bookingId, Long userId, boolean approved) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isEmpty()) {
            throw new BookingNotFoundException("Booking not found" + bookingId);
        }

        if (!checkOwner(booking.get().getItem().getOwner().getId(), userId)) {
            throw new BookingNotFoundException("Booker cannot set status to the booking");
        }

        if (booking.get().getStatus().equals(BookingStatus.APPROVED)) {
            throw new UnavailableBookingException("Status is already set APPROVED");
        }

        if (approved) {
            booking.get().setStatus(BookingStatus.APPROVED);
        } else {
            booking.get().setStatus(BookingStatus.REJECTED);
        }
        return BookingMapper.toDto(bookingRepository.save(booking.get()));
    }

    @Override
    public BookingDto getById(Long bookingId, Long userId) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isEmpty()) {
            log.error(String.format("No booking with id %d is found", bookingId));
            throw new BookingNotFoundException(String.format("Booking with id %d is not found", bookingId));
        }

        if (!checkBookerRights(booking.get().getBooker().getId(), userId)
                && !checkOwner(booking.get().getItem().getOwner().getId(), userId)) {
            throw new BookingNotFoundException("no rights for this booking");
        }
        log.info(String.format("Booking with id %d is found", bookingId));
        return BookingMapper.toDto(booking.get());
    }

    @Override
    public List<BookingDto> findAllByUserId(Long userId, BookingState state) {
        userService.findById(userId);

        switch (state) {
            case ALL:
                return BookingMapper.toDtoList(bookingRepository.findAllByBookerIdOrderByStartDesc(userId));
            case CURRENT:
                return BookingMapper.toDtoList(bookingRepository.
                        findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                                userId, LocalDateTime.now(), LocalDateTime.now()));
            case PAST:
                return BookingMapper.toDtoList(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(
                        userId, LocalDateTime.now()));
            case FUTURE:
                return BookingMapper.toDtoList(bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(
                        userId, LocalDateTime.now()));
            case WAITING:
                return BookingMapper.toDtoList(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                        userId, BookingStatus.WAITING));
            case REJECTED:
                return BookingMapper.toDtoList(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                        userId, BookingStatus.REJECTED));
            default:
                throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @Override
    public List<BookingDto> findAllByOwnerId(Long ownerId, BookingState state) {
        userService.findById(ownerId);
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case ALL:
                return BookingMapper.toDtoList(bookingRepository.findAllByOwnerId(ownerId));
            case CURRENT:
                return BookingMapper.toDtoList(bookingRepository.findCurrentByOwnerId(ownerId, now, now));
            case PAST:
                return BookingMapper.toDtoList(bookingRepository.findPastByOwnerId(ownerId, now));
            case FUTURE:
                return BookingMapper.toDtoList(bookingRepository.findFutureByOwnerId(ownerId, now));
            case WAITING:
                return BookingMapper.toDtoList(bookingRepository.findWaitingByOwnerId(ownerId));
            case REJECTED:
                return BookingMapper.toDtoList(bookingRepository.findRejectedByOwnerId(ownerId));
            default:
                throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    private void checkDates(LocalDateTime start, LocalDateTime end) {
        if (end.isBefore(start)) {
            throw new UnavailableBookingException("End date should be after the start date");
        }
    }

    private boolean checkBookerRights(Long userId, Long bookerId) {
        return Objects.equals(userId, bookerId);
    }

    private boolean checkOwner(Long userId, Long itemOwnerId) {
        return Objects.equals(userId, itemOwnerId);
    }
}
