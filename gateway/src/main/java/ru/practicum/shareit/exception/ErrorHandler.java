package ru.practicum.shareit.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(UnsupportedStatusException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUnsupportedStatusException(final UnsupportedStatusException e) {
        return new ErrorResponse(e.getMessage());
    }

    @Getter
    @AllArgsConstructor
    static class ErrorResponse {
        private final String error;
    }
}
