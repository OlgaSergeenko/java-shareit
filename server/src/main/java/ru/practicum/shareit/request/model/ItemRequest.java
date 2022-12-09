package ru.practicum.shareit.request.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.item.model.Item;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "REQUEST")
@Builder
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 250)
    private String description;
    @ManyToOne
    @JoinColumn(name = "requestor_id", nullable = false)
    private User requestor;
    @Column(name = "creation_date", nullable = false)
    private LocalDateTime created;
    @OneToMany(mappedBy = "itemRequest")
    @ToString.Exclude
    private List<Item> items;
}
