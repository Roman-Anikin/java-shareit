package shareit.app.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import shareit.app.requests.dto.ItemRequestDto;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@Validated
@AllArgsConstructor
public class ItemRequestController {

    private ItemRequestClient client;

    @PostMapping
    public Mono<ItemRequestDto> add(@RequestHeader("X-Sharer-User-Id") Long requesterId,
                                    @Valid @RequestBody ItemRequestDto requestDto) {
        return client.add(requesterId, requestDto);
    }

    @GetMapping
    public Mono<List<ItemRequestDto>> getAllByRequester(@RequestHeader("X-Sharer-User-Id") Long requesterId) {
        return client.getAllByRequester(requesterId);
    }

    @GetMapping("/all")
    public Mono<List<ItemRequestDto>> getAllExceptRequester(@RequestHeader("X-Sharer-User-Id") Long requesterId,
                                                            @RequestParam(value = "from", defaultValue = "0")
                                                            @PositiveOrZero Integer from,
                                                            @RequestParam(value = "size", defaultValue = "200")
                                                            @Positive Integer size) {
        return client.getAllExceptRequester(requesterId, from, size);
    }

    @GetMapping("/{requestId}")
    public Mono<ItemRequestDto> getById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long requestId) {
        return client.getById(userId, requestId);
    }
}
