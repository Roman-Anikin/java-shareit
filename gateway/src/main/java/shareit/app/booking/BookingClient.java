package shareit.app.booking;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import shareit.app.booking.dto.BookingDto;

import java.util.List;

@Component
public class BookingClient {

    private final WebClient client;
    private final String sharerHeader = "X-Sharer-User-Id";

    public BookingClient(@Value("${share-it-server.url}/bookings") String url) {
        client = WebClient.builder()
                .baseUrl(url)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public Mono<BookingDto> add(Long userId, BookingDto bookingDto) {
        return client.post()
                .header(sharerHeader, String.valueOf(userId))
                .bodyValue(bookingDto)
                .retrieve()
                .bodyToMono(BookingDto.class);
    }

    public Mono<BookingDto> makeApprove(Long userId, Long bookingId, boolean approved) {
        return client.patch()
                .uri(uriBuilder -> uriBuilder
                        .path("/{bookingId}")
                        .queryParam("approved", approved)
                        .build(bookingId))
                .header(sharerHeader, String.valueOf(userId))
                .retrieve()
                .bodyToMono(BookingDto.class);
    }

    public Mono<BookingDto> getById(Long userId, Long bookingId) {
        return client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/{bookingId}")
                        .build(bookingId))
                .header(sharerHeader, String.valueOf(userId))
                .retrieve()
                .bodyToMono(BookingDto.class);
    }

    public Mono<List<BookingDto>> getByUserAndState(Long userId, String state, Integer from, Integer size) {
        return client.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("state", state)
                        .queryParam("from", from)
                        .queryParam("size", size)
                        .build())
                .header(sharerHeader, String.valueOf(userId))
                .retrieve()
                .bodyToFlux(BookingDto.class)
                .collectList();
    }

    public Mono<List<BookingDto>> getByOwnerAndState(Long userId, String state, Integer from, Integer size) {
        return client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/owner")
                        .queryParam("state", state)
                        .queryParam("from", from)
                        .queryParam("size", size)
                        .build())
                .header(sharerHeader, String.valueOf(userId))
                .retrieve()
                .bodyToFlux(BookingDto.class)
                .collectList();
    }
}
