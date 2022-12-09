package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.enumerated.BookingState;

import javax.validation.Valid;
import java.util.List;


@Validated
@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDto> create(@RequestBody BookingDto bookingDto,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        BookingDto bookingSaved = bookingService.create(bookingDto, userId);
        log.info(String.format("Booking with id %d is created", bookingSaved.getId()));
        return ResponseEntity.ok(bookingSaved);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> setBookingStatus(@PathVariable("bookingId") Long bookingId,
                                                       @RequestHeader("X-Sharer-User-Id") Long userId,
                                                       @RequestParam boolean approved) {
        BookingDto booking = bookingService.setBookingStatus(bookingId, userId, approved);
        return ResponseEntity.ok(booking);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getById(@PathVariable("bookingId") Long bookingId,
                                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(bookingService.getById(bookingId, userId));
    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> findAllByUserId(
            @RequestParam(required = false, defaultValue = "ALL") BookingState state,
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(required = false) Integer from,
            @RequestParam(required = false) Integer size) {
        if (from == null && size == null) {
            return ResponseEntity.ok(bookingService.findAllByUserId(userId, state));
        }
        return ResponseEntity.ok(bookingService.findAllByUserId(userId, from, size));
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDto>> findAllByOwnerId(
            @RequestParam(required = false, defaultValue = "ALL") BookingState state,
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(required = false) Integer from,
            @RequestParam(required = false) Integer size) {
        if (from == null && size == null) {
            return ResponseEntity.ok(bookingService.findAllByOwnerId(userId, state));
        }
        return ResponseEntity.ok(bookingService.findAllByOwnerId(userId, from, size));
    }}
