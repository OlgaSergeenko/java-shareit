package ru.practicum.shareit.booking.model;

import lombok.*;
import ru.practicum.shareit.enumerated.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "BOOKING")
@Builder
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "start_date", nullable = false)
    @FutureOrPresent
    private LocalDateTime start;
    @Column(name = "end_date", nullable = false)
    @Future
    private LocalDateTime end;
    @ManyToOne
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    private Item item;
    @ManyToOne
    @JoinColumn(name = "booker_id", referencedColumnName = "id")
    private User booker;
    @Enumerated(EnumType.STRING)
    private BookingStatus status;
}
