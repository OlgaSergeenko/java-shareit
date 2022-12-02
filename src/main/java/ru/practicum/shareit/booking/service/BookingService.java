package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.enumerated.BookingState;

import java.util.List;


public interface BookingService {

    BookingDto create(BookingDto bookingDto, Long userId);

    BookingDto setBookingStatus(Long bookingId, Long userId, boolean approved);

    BookingDto getById(Long bookingId, Long userId);

    List<BookingDto> findAllByUserId(Long userId, BookingState stat);

    List<BookingDto> findAllByOwnerId(Long ownerId, BookingState state);

    List<BookingDto> findAllByUserId(Long userId, Integer from, Integer size);

    List<BookingDto> findAllByOwnerId(Long userId, Integer from, Integer size);
}
