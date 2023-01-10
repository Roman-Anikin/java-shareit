package ru.practicum.shareit.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@ToString
public class ErrorResponse {

    private final List<String> errors;
    private final String error;
    private final String reason;
    private final String status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime timestamp;

    public ErrorResponse(Exception ex, String error, String reason, HttpStatus status) {
        this.errors = List.of(String.valueOf(ex.getClass()));
        this.error = error;
        this.reason = reason;
        this.status = status.toString();
        this.timestamp = LocalDateTime.now();
    }
}
