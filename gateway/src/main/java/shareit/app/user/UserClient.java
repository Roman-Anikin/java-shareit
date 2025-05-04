package shareit.app.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import shareit.app.user.dto.UserDto;

import java.util.List;

@Component
public class UserClient {

    private final WebClient client;

    public UserClient(@Value("${share-it-server.url}/users") String url) {
        client = WebClient.builder()
                .baseUrl(url)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public Mono<UserDto> add(UserDto userDto) {
        return client.post()
                .bodyValue(userDto)
                .retrieve()
                .bodyToMono(UserDto.class);
    }

    public Mono<UserDto> update(UserDto userDto, Long userId) {
        return client.patch()
                .uri(uriBuilder -> uriBuilder
                        .path("/{userId}")
                        .build(userId))
                .bodyValue(userDto)
                .retrieve()
                .bodyToMono(UserDto.class);
    }

    public Mono<Void> delete(Long userId) {
        return client.delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/{userId}")
                        .build(userId))
                .retrieve()
                .bodyToMono(Void.class);
    }

    public Mono<UserDto> getById(Long userId) {
        return client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/{userId}")
                        .build(userId))
                .retrieve()
                .bodyToMono(UserDto.class);
    }

    public Mono<List<UserDto>> getAll() {
        return client.get()
                .retrieve()
                .bodyToFlux(UserDto.class)
                .collectList();
    }
}
