package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;


@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "item", schema = "public")
@Builder
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false, length = 250)
    private String name;
    @Column(nullable = false, length = 250)
    private String description;
    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable;
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
}
