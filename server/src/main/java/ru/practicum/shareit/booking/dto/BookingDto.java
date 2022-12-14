package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.enumerated.BookingStatus;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.user.dto.UserShortDto;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
    private ItemShortDto item;
    private UserShortDto booker;
    private BookingStatus status;
}
