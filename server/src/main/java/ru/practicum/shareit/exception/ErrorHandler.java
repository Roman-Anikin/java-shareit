package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice("ru.practicum.shareit")
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleObjectNotFoundException(final ObjectNotFoundException e) {
        log.info("Exception: {}", e.toString());
        return new ErrorResponse(e, e.getMessage(), "Ошибка запроса", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleObjectAlreadyExistException(final ObjectAlreadyExistException e) {
        log.info("Exception: {}", e.toString());
        return new ErrorResponse(e, e.getMessage(), "Ошибка запроса", HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final ValidationException e) {
        log.info("Exception: {}", e.toString());
        return new ErrorResponse(e, e.getMessage(), "Ошибка запроса", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleConstraintViolationException(final ConstraintViolationException e) {
        log.info("Exception: {}", e.toString());
        return new ErrorResponse(e, e.getSQLException().getMessage(), "Ошибка приложения",
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}