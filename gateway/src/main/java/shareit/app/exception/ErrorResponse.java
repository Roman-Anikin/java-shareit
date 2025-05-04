package shareit.app.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatusCode;

import java.time.LocalDateTime;

@Getter
@ToString
public class ErrorResponse {

    private final String error;
    private final String reason;
    private final String status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime timestamp;

    public ErrorResponse(Exception ex, String reason, HttpStatusCode status) {
        this.error = String.valueOf(ex);
        this.reason = reason;
        this.status = status.toString();
        this.timestamp = LocalDateTime.now();
    }
}
