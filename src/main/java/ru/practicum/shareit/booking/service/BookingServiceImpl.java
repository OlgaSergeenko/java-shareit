package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import ru.practicum.shareit.user.dto.UserDto;
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

    private BookingRepository bookingRepository;
    private UserServiceImpl userService;
    private ItemService itemService;

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
        UserDto userDto = userService.findUserIfExistOrElseThrowNotFound(userId);
        User user = UserMapper.toUser(userDto);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        return BookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto setBookingStatus(Long bookingId, Long userId, boolean approved) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isEmpty()) {
            throw new BookingNotFoundException("Booking not found " + bookingId);
        }
        Booking bookingFound = booking.get();
        if (!checkOwner(bookingFound.getItem().getOwner().getId(), userId)) {
            throw new BookingNotFoundException("Booker cannot set status to the booking");
        }

        if (bookingFound.getStatus().equals(BookingStatus.APPROVED)) {
            throw new UnavailableBookingException("Status is already set APPROVED");
        }

        if (approved) {
            bookingFound.setStatus(BookingStatus.APPROVED);
        } else {
            bookingFound.setStatus(BookingStatus.REJECTED);
        }
        return BookingMapper.toDto(bookingRepository.save(bookingFound));
    }

    @Override
    public BookingDto getById(Long bookingId, Long userId) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isEmpty()) {
            log.error(String.format("No booking with id %d is found", bookingId));
            throw new BookingNotFoundException(String.format("Booking with id %d is not found", bookingId));
        }

        Booking bookingFound = booking.get();
        if (!checkBookerRights(bookingFound.getBooker().getId(), userId)
                && !checkOwner(bookingFound.getItem().getOwner().getId(), userId)) {
            throw new BookingNotFoundException("no rights for this booking");
        }
        log.info(String.format("Booking with id %d is found", bookingId));
        return BookingMapper.toDto(bookingFound);
    }

    @Override
    public List<BookingDto> findAllByUserId(Long userId, BookingState state) {
        userService.findUserIfExistOrElseThrowNotFound(userId);
        LocalDateTime now = LocalDateTime.now();

        switch (state) {
            case ALL:
                return BookingMapper.toDtoList(bookingRepository.findAllByBookerIdOrderByStartDesc(userId));
            case CURRENT:
                return BookingMapper.toDtoList(bookingRepository
                        .findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                                userId, now, now));
            case PAST:
                return BookingMapper.toDtoList(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(
                        userId, now));
            case FUTURE:
                return BookingMapper.toDtoList(bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(
                        userId, now));
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
        userService.findUserIfExistOrElseThrowNotFound(ownerId);
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case ALL:
                return BookingMapper.toDtoList(bookingRepository.findAllByItem_OwnerIdOrderByStartDesc(ownerId));
            case CURRENT:
                return BookingMapper.toDtoList(bookingRepository
                        .findAllByItem_OwnerIdAndStartLessThanEqualAndEndGreaterThanOrderByStart(ownerId, now, now));
            case PAST:
                return BookingMapper.toDtoList(bookingRepository
                        .findAllByItem_OwnerIdAndEndLessThanEqual(ownerId, now));
            case FUTURE:
                return BookingMapper.toDtoList(bookingRepository
                        .findAllByItem_OwnerIdAndStartGreaterThanEqualOrderByStartDesc(ownerId, now));
            case WAITING:
                return BookingMapper.toDtoList(bookingRepository
                        .findAllByItem_OwnerIdAndStatusOrderByStart(ownerId, BookingStatus.WAITING));
            case REJECTED:
                return BookingMapper.toDtoList(bookingRepository
                        .findAllByItem_OwnerIdAndStatus(ownerId, BookingStatus.REJECTED));
            default:
                throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @Override
    public List<BookingDto> findAllByUserId(Long userId, Integer from, Integer size) {
        Pageable page = PageRequest.of((int) from / size, size);
        return BookingMapper.toDtoList(bookingRepository.findAllByBookerIdOrderByStartDesc(userId, page));
    }

    @Override
    public List<BookingDto> findAllByOwnerId(Long userId, Integer from, Integer size) {
        Pageable page = PageRequest.of((int) from / size, size);
        return BookingMapper.toDtoList(bookingRepository.findAllByItem_OwnerIdOrderByStartDesc(userId, page));
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
