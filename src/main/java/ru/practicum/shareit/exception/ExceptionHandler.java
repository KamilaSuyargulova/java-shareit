package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse validationExceptionHandler(final ValidationException e) {
        return new ErrorResponse("Ошибка валидации", e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse entityNotFoundExceptionHandler(final EntityNotFoundException e) {
        return new ErrorResponse("Ошибка поиска", e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse generalExceptionHandler(final RuntimeException e) {
        return new ErrorResponse("Ошибка", e.getMessage());
    }
}