package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.UnsupportedStatusException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getBookings(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(name = "state", required = false, defaultValue = "all") String stateParam,
            @RequestParam(name = "from", required = false) @Positive Integer from,
            @RequestParam(name = "size", required = false) @PositiveOrZero Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new UnsupportedStatusException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        if (from != null && size != null) {
            return bookingClient.getBookings(userId, state, from, size);
        }
        return bookingClient.getBookingsNoPagination(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwnerId(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(name = "state", required = false, defaultValue = "all") String stateParam,
            @RequestParam(name = "from", required = false) @Positive Integer from,
            @RequestParam(name = "size", required = false) @PositiveOrZero Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new UnsupportedStatusException("Unknown state: " + stateParam));
        log.info("Get bookings for owner {} with state {}, from={}, size={}", userId, stateParam, from, size);
        if (from != null && size != null) {
            return bookingClient.getBookingsForOwner(userId, state, from, size);
        }
        return bookingClient.getBookingsNoPaginationForOwner(userId, state);

    }

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestBody @Valid BookItemRequestDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.bookItem(userId, requestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> setBookingStatus(@PathVariable("bookingId") Long bookingId,
                                                   @RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @RequestParam boolean approved) {
        log.info("Setting status to booking {}", bookingId);
        return bookingClient.setBookingStatus(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }
}
