package shareit.app.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import shareit.app.booking.dto.BookingDto;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@Validated
@AllArgsConstructor
public class BookingController {

    private BookingClient client;

    @PostMapping
    public Mono<BookingDto> add(@RequestHeader("X-Sharer-User-Id") Long userId,
                                @Valid @RequestBody BookingDto bookingDto) {
        return client.add(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public Mono<BookingDto> makeApprove(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @PathVariable Long bookingId,
                                        @RequestParam(value = "approved") boolean approved) {
        return client.makeApprove(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public Mono<BookingDto> getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @PathVariable Long bookingId) {
        return client.getById(userId, bookingId);
    }

    @GetMapping
    public Mono<List<BookingDto>> getByUserAndState(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestParam(value = "state", defaultValue = "ALL") String state,
                                                    @RequestParam(value = "from", defaultValue = "0")
                                                    @PositiveOrZero Integer from,
                                                    @RequestParam(value = "size", defaultValue = "200")
                                                    @Positive Integer size) {
        return client.getByUserAndState(userId, state, from, size);
    }

    @GetMapping("/owner")
    public Mono<List<BookingDto>> getByOwnerAndState(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam(value = "state", defaultValue = "ALL") String state,
                                                     @RequestParam(value = "from", defaultValue = "0")
                                                     @PositiveOrZero Integer from,
                                                     @RequestParam(value = "size", defaultValue = "200")
                                                     @Positive Integer size) {
        return client.getByOwnerAndState(userId, state, from, size);
    }
}
