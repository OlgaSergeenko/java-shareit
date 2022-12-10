package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UnavailableBookingException extends RuntimeException {

    public UnavailableBookingException(String message) {
        super(message);
    }
}
