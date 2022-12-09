package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenAccessException extends RuntimeException {

    public ForbiddenAccessException(final String message) {
        super(message);
    }
}
