package shareit.app.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import shareit.app.user.dto.UserDto;

import java.util.List;

import static org.mockito.Mockito.*;

@WebFluxTest(UserController.class)
public class UserControllerTest {

    private static final String URI = "/users";
    @Autowired
    private WebTestClient testClient;
    @MockitoBean
    private UserClient userClient;

    @Test
    void addUser() {
        UserDto user = new UserDto(1L, "test", "qwe@mail.com");
        when(userClient.add(any(UserDto.class))).thenReturn(Mono.just(user));

        testClient.post()
                .uri(URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(user)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.name").isEqualTo("test");

        verify(userClient).add(any(UserDto.class));
        verifyNoMoreInteractions(userClient);
    }

    @Test
    void addWithMailExist() {
        UserDto user = new UserDto(1L, "test", "qwe@mail.com");
        when(userClient.add(any(UserDto.class)))
                .thenReturn(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST)));

        testClient.post()
                .uri(URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(user)
                .exchange()
                .expectStatus().isBadRequest();

        verify(userClient).add(any(UserDto.class));
        verifyNoMoreInteractions(userClient);
    }

    @Test
    void addWithoutMail() {
        UserDto user = new UserDto(1L, "user", null);
        when(userClient.add(any(UserDto.class)))
                .thenReturn(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST)));

        testClient.post()
                .uri(URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(user)
                .exchange()
                .expectStatus().isBadRequest();

        verify(userClient).add(any(UserDto.class));
        verifyNoMoreInteractions(userClient);
    }

    @Test
    void addWithInvalidMail() {
        UserDto user = new UserDto(1L, "user", "qwe");

        testClient.post()
                .uri(URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(user)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void update() {
        UserDto user = new UserDto(1L, "test", "qwe@mail.com");
        when(userClient.update(any(UserDto.class), any()))
                .thenReturn(Mono.just(user));

        testClient.patch()
                .uri(uriBuilder -> uriBuilder
                        .path(URI + "/{userId}")
                        .build(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(user)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.name").isEqualTo(user.getName());

        verify(userClient).update(any(UserDto.class), any());
        verifyNoMoreInteractions(userClient);
    }

    @Test
    void updateNameOnly() {
        UserDto user = new UserDto(null, "test", null);
        when(userClient.update(any(UserDto.class), any()))
                .thenReturn(Mono.just(user));

        testClient.patch()
                .uri(uriBuilder -> uriBuilder
                        .path(URI + "/{userId}")
                        .build(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(user)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.name").isEqualTo(user.getName());

        verify(userClient).update(any(UserDto.class), any());
        verifyNoMoreInteractions(userClient);
    }

    @Test
    void updateMailOnly() {
        UserDto user = new UserDto(null, null, "qwe@mail.com");
        when(userClient.update(any(UserDto.class), any()))
                .thenReturn(Mono.just(user));

        testClient.patch()
                .uri(uriBuilder -> uriBuilder
                        .path(URI + "/{userId}")
                        .build(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(user)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.email").isEqualTo(user.getEmail());

        verify(userClient).update(any(UserDto.class), any());
        verifyNoMoreInteractions(userClient);
    }

    @Test
    void updateWithMailExist() {
        UserDto user = new UserDto(1L, "user", "qwe@mail.com");
        when(userClient.update(any(UserDto.class), any()))
                .thenReturn(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST)));

        testClient.patch()
                .uri(uriBuilder -> uriBuilder
                        .path(URI + "/{userId}")
                        .build(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(user)
                .exchange()
                .expectStatus().isBadRequest();

        verify(userClient).update(any(UserDto.class), any());
        verifyNoMoreInteractions(userClient);
    }

    @Test
    void updateWithInvalidMail() {
        UserDto user = new UserDto(1L, "user", "qwe");

        testClient.patch()
                .uri(uriBuilder -> uriBuilder
                        .path(URI + "/{userId}")
                        .build(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(user)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void delete() {
        when(userClient.delete(any())).thenReturn(Mono.empty());

        testClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path(URI + "/{userId}")
                        .build(1L))
                .exchange()
                .expectStatus().isOk();

        verify(userClient).delete(any());
        verifyNoMoreInteractions(userClient);
    }

    @Test
    void getById() {
        UserDto user = new UserDto(1L, "test", "qwe@mail.com");
        when(userClient.getById(any())).thenReturn(Mono.just(user));

        testClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(URI + "/{userId}")
                        .build(1L))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.name").isEqualTo(user.getName());

        verify(userClient).getById(any());
        verifyNoMoreInteractions(userClient);
    }

    @Test
    void getAll() {
        UserDto user = new UserDto(1L, "user", "qwe@mail.com");
        UserDto user2 = new UserDto(2L, "new user", "asd@mail.com");
        when(userClient.getAll()).thenReturn(Mono.just(List.of(user, user2)));

        testClient.get()
                .uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(UserDto.class)
                .hasSize(2);

        verify(userClient).getAll();
        verifyNoMoreInteractions(userClient);
    }
}
