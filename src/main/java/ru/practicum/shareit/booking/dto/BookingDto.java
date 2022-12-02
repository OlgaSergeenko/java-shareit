package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.enumerated.BookingStatus;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.user.dto.UserShortDto;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BookingDto {
    private Long id;
    @FutureOrPresent
    private LocalDateTime start;
    @Future
    private LocalDateTime end;
    private Long itemId;
    private ItemShortDto item;
    private UserShortDto booker;
    private BookingStatus status;
}
