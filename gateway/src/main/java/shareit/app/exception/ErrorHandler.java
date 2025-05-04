package shareit.app.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@RestControllerAdvice("shareit.app")
public class ErrorHandler extends DefaultResponseErrorHandler {

    @ExceptionHandler(WebClientResponseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse webClientResponseExceptionHandler(WebClientResponseException e) {
        return new ErrorResponse(e, "Ошибка запроса", e.getStatusCode());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolationException(final ConstraintViolationException e) {
        return new ErrorResponse(e, "Ошибка приложения", HttpStatus.BAD_REQUEST);
    }
}
