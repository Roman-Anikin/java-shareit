package shareit.app.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import shareit.app.booking.dto.BookingDto;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WebFluxTest(BookingController.class)
public class BookingControllerTest {

    private static final String URI = "/bookings";
    private static final String HEADER = "X-Sharer-User-Id";
    @Autowired
    private WebTestClient testClient;
    @MockitoBean
    private BookingClient bookingClient;

    @Test
    void addBooking() {
        BookingDto dto = new BookingDto(1L, getLTD("+", 2), getLTD("+", 3),
                null, 1L, null, 1L, BookingStatus.WAITING);
        when(bookingClient.add(any(), any())).thenReturn(Mono.just(dto));

        testClient.post()
                .uri(URI)
                .header(HEADER, String.valueOf(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.itemId").isEqualTo(dto.getItemId())
                .jsonPath("$.bookerId").isEqualTo(dto.getBookerId());

        verify(bookingClient).add(any(), any(BookingDto.class));
        verifyNoMoreInteractions(bookingClient);
    }

    @Test
    void addBookingWithoutHeader() {
        BookingDto dto = new BookingDto(1L, getLTD("+", 2), getLTD("+", 3),
                null, 1L, null, 1L, BookingStatus.WAITING);

        testClient.post()
                .uri(URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void addBookingWithoutStart() {
        BookingDto dto = new BookingDto(1L, null, getLTD("+", 3),
                null, 1L, null, 1L, BookingStatus.WAITING);

        testClient.post()
                .uri(URI)
                .header(HEADER, String.valueOf(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void addBookingWithoutEnd() {
        BookingDto dto = new BookingDto(1L, getLTD("+", 2), null,
                null, 1L, null, 1L, BookingStatus.WAITING);

        testClient.post()
                .uri(URI)
                .header(HEADER, String.valueOf(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void addBookingWithStartInPast() {
        BookingDto dto = new BookingDto(1L, getLTD("-", 1), getLTD("+", 3),
                null, 1L, null, 1L, BookingStatus.WAITING);

        testClient.post()
                .uri(URI)
                .header(HEADER, String.valueOf(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void addBookingWithEndInPast() {
        BookingDto dto = new BookingDto(1L, getLTD("+", 2), getLTD("-", 3),
                null, 1L, null, 1L, BookingStatus.WAITING);

        testClient.post()
                .uri(URI)
                .header(HEADER, String.valueOf(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void makeApprove() {
        BookingDto dto = new BookingDto(1L, getLTD("+", 2), getLTD("+", 3),
                null, 1L, null, 1L, BookingStatus.APPROVED);
        when(bookingClient.makeApprove(any(), any(), eq(true))).thenReturn(Mono.just(dto));

        testClient.patch()
                .uri(uriBuilder -> uriBuilder
                        .path(URI + "/{bookingId}")
                        .queryParam("approved", true)
                        .build(dto.getId()))
                .header(HEADER, String.valueOf(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.itemId").isEqualTo(dto.getItemId())
                .jsonPath("$.bookerId").isEqualTo(dto.getBookerId());

        verify(bookingClient).makeApprove(any(), any(), eq(true));
        verifyNoMoreInteractions(bookingClient);
    }

    @Test
    void makeApproveWithoutHeader() {
        BookingDto dto = new BookingDto(1L, getLTD("+", 2), getLTD("+", 3),
                null, 1L, null, 1L, BookingStatus.APPROVED);
        when(bookingClient.makeApprove(any(), any(), eq(true))).thenReturn(Mono.just(dto));

        testClient.patch()
                .uri(uriBuilder -> uriBuilder
                        .path(URI + "/{bookingId}")
                        .queryParam("approved", true)
                        .build(dto.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void makeApproveWithoutParam() {
        BookingDto dto = new BookingDto(1L, getLTD("+", 2), getLTD("+", 3),
                null, 1L, null, 1L, BookingStatus.APPROVED);
        when(bookingClient.makeApprove(any(), any(), eq(true))).thenReturn(Mono.just(dto));

        testClient.patch()
                .uri(uriBuilder -> uriBuilder
                        .path(URI + "/{bookingId}")
                        .build(dto.getId()))
                .header(HEADER, String.valueOf(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void getById() {
        BookingDto dto = new BookingDto(1L, getLTD("+", 2), getLTD("+", 3),
                null, 1L, null, 1L, BookingStatus.WAITING);
        when(bookingClient.getById(any(), any())).thenReturn(Mono.just(dto));

        testClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(URI + "/{bookingId}")
                        .build(dto.getId()))
                .header(HEADER, String.valueOf(1L))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.itemId").isEqualTo(dto.getItemId())
                .jsonPath("$.bookerId").isEqualTo(dto.getBookerId());

        verify(bookingClient).getById(any(), any());
        verifyNoMoreInteractions(bookingClient);
    }

    @Test
    void getByIdWithoutHeader() {
        testClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(URI + "/{bookingId}")
                        .build(1L))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void getByUserAndState() {
        BookingDto dto = new BookingDto(1L, getLTD("+", 2), getLTD("+", 3),
                null, 1L, null, 1L, BookingStatus.WAITING);
        when(bookingClient.getByUserAndState(any(), any(), any(), any()))
                .thenReturn(Mono.just(List.of(dto)));

        testClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(URI)
                        .queryParam("state", "ALL")
                        .queryParam("from", 0)
                        .queryParam("size", 5)
                        .build())
                .header(HEADER, String.valueOf(1L))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(BookingDto.class)
                .hasSize(1);

        verify(bookingClient).getByUserAndState(any(), any(), any(), any());
        verifyNoMoreInteractions(bookingClient);
    }

    @Test
    void getByUserAndStateWithoutHeader() {
        testClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(URI)
                        .queryParam("state", "ALL")
                        .queryParam("from", 0)
                        .queryParam("size", 5)
                        .build())
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void getByUserAndStateWithoutParams() {
        BookingDto bookingDto = new BookingDto(1L, getLTD("+", 2), getLTD("+", 3),
                null, 1L, null, 1L, BookingStatus.WAITING);
        when(bookingClient.getByUserAndState(any(), any(), any(), any()))
                .thenReturn(Mono.just(List.of(bookingDto)));

        testClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(URI)
                        .build())
                .header(HEADER, String.valueOf(1L))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(BookingDto.class)
                .hasSize(1);

        verify(bookingClient).getByUserAndState(any(), any(), any(), any());
        verifyNoMoreInteractions(bookingClient);
    }

    @Test
    void getByUserAndStateWithFromLessZero() {
        testClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(URI)
                        .queryParam("state", "ALL")
                        .queryParam("from", -1)
                        .queryParam("size", 5)
                        .build())
                .header(HEADER, String.valueOf(1L))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void getByUserAndStateWithSizeZero() {
        testClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(URI)
                        .queryParam("state", "ALL")
                        .queryParam("from", 0)
                        .queryParam("size", 0)
                        .build())
                .header(HEADER, String.valueOf(1L))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void getByUserAndStateWithSizeLessZero() {
        testClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(URI)
                        .queryParam("state", "ALL")
                        .queryParam("from", 0)
                        .queryParam("size", -10)
                        .build())
                .header(HEADER, String.valueOf(1L))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void getByOwnerAndState() {
        BookingDto dto = new BookingDto(1L, getLTD("+", 2), getLTD("+", 3),
                null, 1L, null, 1L, BookingStatus.WAITING);
        when(bookingClient.getByOwnerAndState(any(), any(), any(), any()))
                .thenReturn(Mono.just(List.of(dto)));

        testClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(URI + "/owner")
                        .queryParam("state", "ALL")
                        .queryParam("from", 0)
                        .queryParam("size", 5)
                        .build())
                .header(HEADER, String.valueOf(1L))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(BookingDto.class)
                .hasSize(1);

        verify(bookingClient).getByOwnerAndState(any(), any(), any(), any());
        verifyNoMoreInteractions(bookingClient);
    }

    @Test
    void getByOwnerAndStateWithoutHeader() {
        testClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(URI + "/owner")
                        .queryParam("state", "ALL")
                        .queryParam("from", 0)
                        .queryParam("size", 5)
                        .build())
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void getByOwnerAndStateWithoutParams() {
        BookingDto dto = new BookingDto(1L, getLTD("+", 2), getLTD("+", 3),
                null, 1L, null, 1L, BookingStatus.WAITING);
        when(bookingClient.getByOwnerAndState(any(), any(), any(), any()))
                .thenReturn(Mono.just(List.of(dto)));

        testClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(URI + "/owner")
                        .build())
                .header(HEADER, String.valueOf(1L))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(BookingDto.class)
                .hasSize(1);

        verify(bookingClient).getByOwnerAndState(any(), any(), any(), any());
        verifyNoMoreInteractions(bookingClient);
    }

    @Test
    void getByOwnerAndStateWithFromLessZero() {
        testClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(URI + "/owner")
                        .queryParam("state", "ALL")
                        .queryParam("from", -1)
                        .queryParam("size", 5)
                        .build())
                .header(HEADER, String.valueOf(1L))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void getByOwnerAndStateWithSizeZero() {
        testClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(URI + "/owner")
                        .queryParam("state", "ALL")
                        .queryParam("from", 0)
                        .queryParam("size", 0)
                        .build())
                .header(HEADER, String.valueOf(1L))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void getByOwnerAndStateWithSizeLessZero() {
        testClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(URI + "/owner")
                        .queryParam("state", "ALL")
                        .queryParam("from", 0)
                        .queryParam("size", -10)
                        .build())
                .header(HEADER, String.valueOf(1L))
                .exchange()
                .expectStatus().isBadRequest();
    }

    private LocalDateTime getLTD(String sign, int sec) {
        if (sign.equals("+")) {
            return LocalDateTime.now().plusSeconds(sec).truncatedTo(ChronoUnit.SECONDS);
        } else {
            return LocalDateTime.now().minusSeconds(sec).truncatedTo(ChronoUnit.SECONDS);
        }
    }
}
