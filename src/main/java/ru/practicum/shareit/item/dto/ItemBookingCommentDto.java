package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.comments.CommentDto;

import java.util.List;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemBookingCommentDto {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingShortDto lastBooking;
    private BookingShortDto nextBooking;
    private List<CommentDto> comments;
}
