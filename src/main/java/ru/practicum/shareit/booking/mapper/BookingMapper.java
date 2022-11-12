package ru.practicum.shareit.booking.mapper;


import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.user.dto.UserShortDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {

    public static BookingDto toDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getId(),
                new ItemShortDto(booking.getItem().getId(), booking.getItem().getName()),
                new UserShortDto(booking.getBooker().getId()),
                booking.getStatus()
        );
    }

    public static List<BookingDto> toDtoList(List<Booking> bookings) {
        return bookings.stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }

    public static Booking toBooking (BookingDto bookingDto) {
        return new Booking(
                bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd(),
                Item.builder().id(bookingDto.getId()).build(),
                User.builder().id(bookingDto.getBooker().getId()).build(),
                bookingDto.getStatus()
        );
    }

    public static BookingShortDto toShortDto (Booking booking) {
        return new BookingShortDto(
                booking.getId(),
                booking.getBooker().getId()
        );
    }
}
