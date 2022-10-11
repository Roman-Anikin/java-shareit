package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Component
public class UserClient {

    private final RestTemplate template;

    public UserClient(@Value("${shareit-server.url}/users") String url, RestTemplateBuilder template) {
        this.template = template.errorHandler(new ErrorHandler())
                .uriTemplateHandler(new DefaultUriBuilderFactory(url))
                .build();
    }

    public ResponseEntity<UserDto> add(UserDto userDto) {
        return template.postForEntity("",
                getHttpEntity(userDto),
                UserDto.class);
    }

    public ResponseEntity<UserDto> update(UserDto userDto, Long userId) {
        return template.exchange("/{userId}",
                HttpMethod.PATCH,
                getHttpEntity(userDto),
                UserDto.class,
                userId);
    }

    public void delete(Long userId) {
        template.delete("/{userId}",
                userId);
    }

    public ResponseEntity<UserDto> getById(Long userId) {
        return template.getForEntity("/{userId}",
                UserDto.class,
                userId);
    }

    public ResponseEntity<List<UserDto>> getAll() {
        return template.exchange("",
                HttpMethod.GET,
                getHttpEntity(null),
                new ParameterizedTypeReference<>() {
                });
    }

    private HttpEntity<UserDto> getHttpEntity(UserDto userDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return userDto == null ? new HttpEntity<>(headers) : new HttpEntity<>(userDto, headers);
    }
}
