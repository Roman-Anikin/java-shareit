package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRepository repository;

    @Test
    public void addUser() {
        UserDto userDto = new UserDto(1L, "user", "qwe@mail.com");
        User user = new User(1L, "user", "qwe@mail.com");
        when(userMapper.convertFromDto(userDto)).thenReturn(user);
        when((userMapper.convertToDto(user))).thenReturn(userDto);
        when(repository.save(any())).thenReturn(user);

        UserDto savedUser = userService.add(userDto);
        assertThat(savedUser).usingRecursiveComparison().isEqualTo(userDto);
    }

    @Test
    public void addUserWithoutMail() {
        UserDto userDto = new UserDto(1L, "user", null);
        assertThatThrownBy(() -> userService.add(userDto))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    public void addUserWithEmptyMail() {
        UserDto userDto = new UserDto(1L, "user", "");
        assertThatThrownBy(() -> userService.add(userDto))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    public void addUserWithBlankMail() {
        UserDto userDto = new UserDto(1L, "user", "   ");
        assertThatThrownBy(() -> userService.add(userDto))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    public void updateUser() {
        UserDto userDto = new UserDto(1L, "new user", "asd@mail.com");
        User user = new User(1L, "user", "qwe@mail.com");
        when(repository.findById(anyLong())).thenReturn(Optional.of(user));
        when((userMapper.convertToDto(user))).thenReturn(userDto);

        UserDto updatedUser = userService.update(userDto.getId(), userDto);
        assertThat(updatedUser).usingRecursiveComparison().isEqualTo(userDto);
    }

    @Test
    public void updateUserWithOnlyName() {
        UserDto userDto = new UserDto(1L, "new user", null);
        User user = new User(1L, "user", "qwe@mail.com");
        when(repository.findById(anyLong())).thenReturn(Optional.of(user));
        when((userMapper.convertToDto(user))).thenReturn(new UserDto(1L, "new user", "qwe@mail.com"));

        UserDto updatedUser = userService.update(userDto.getId(), userDto);
        assertThat(userDto.getId()).isSameAs(updatedUser.getId());
        assertThat(userDto.getName()).isSameAs(updatedUser.getName());
        assertThat(user.getEmail()).isSameAs(updatedUser.getEmail());
    }

    @Test
    public void updateUserWithOnlyMail() {
        UserDto userDto = new UserDto(1L, null, "asd@mail.com");
        User user = new User(1L, "user", "qwe@mail.com");
        when(repository.findById(anyLong())).thenReturn(Optional.of(user));
        when((userMapper.convertToDto(user))).thenReturn(new UserDto(1L, "user", "asd@mail.com"));

        UserDto updatedUser = userService.update(userDto.getId(), userDto);
        assertThat(userDto.getId()).isSameAs(updatedUser.getId());
        assertThat(user.getName()).isSameAs(updatedUser.getName());
        assertThat(userDto.getEmail()).isSameAs(updatedUser.getEmail());
    }

    @Test
    public void updateUserWithWrongId() {
        UserDto userDto = new UserDto(1L, "user", "qwe@mail.com");
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                userService.update(userDto.getId(), userDto))
                .isInstanceOf(ObjectNotFoundException.class);
    }

    @Test
    public void getById() {
        UserDto userDto = new UserDto(1L, "user", "qwe@mail.com");
        User user = new User(1L, "user", "qwe@mail.com");
        when((userMapper.convertToDto(user))).thenReturn(userDto);
        when(repository.findById(anyLong())).thenReturn(Optional.of(user));

        UserDto foundUser = userService.getById(userDto.getId());
        assertThat(foundUser).usingRecursiveComparison().isEqualTo(userDto);
    }

    @Test
    public void getByWrongId() {
        when(repository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                userService.getById(1L))
                .isInstanceOf(ObjectNotFoundException.class);
    }

    @Test
    public void getAll() {
        UserDto userDto = new UserDto(1L, "user", "qwe@mail.com");
        UserDto userDto2 = new UserDto(2L, "user2", "asd@mail.com");
        User user = new User(1L, "user", "qwe@mail.com");
        User user2 = new User(2L, "user2", "asd@mail.com");
        when(repository.findAll()).thenReturn(List.of(user, user2));
        when(userMapper.convertToDto(anyList())).thenReturn(List.of(userDto, userDto2));

        List<UserDto> users = userService.getAll();
        assertThat(users.size()).isEqualTo(2);
        assertThat(users.get(0)).usingRecursiveComparison().isEqualTo(userDto);
        assertThat(users.get(1)).usingRecursiveComparison().isEqualTo(userDto2);
    }

    @Test
    public void delete() {
        when(repository.findById(any())).thenReturn(Optional.of(new User()));
        userService.delete(1L);

        verify(repository, times(1)).deleteById(any());
    }

    @Test
    public void deleteByWrongId() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                userService.delete(1L))
                .isInstanceOf(ObjectNotFoundException.class);
    }
}
