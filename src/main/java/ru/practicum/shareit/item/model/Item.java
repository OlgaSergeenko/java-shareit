package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;


@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class Item {
    private long id;
    @NotNull
    private String name;
    @NotNull
    private String description;
    @NotNull
    private Boolean available;
    @NotNull
    private long ownerId;
}