package shareit.app.requests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import shareit.app.exception.ObjectNotFoundException;
import shareit.app.item.ItemService;
import shareit.app.item.dto.ItemDto;
import shareit.app.requests.dto.ItemRequestDto;
import shareit.app.user.User;
import shareit.app.user.UserService;
import shareit.app.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {

    @InjectMocks
    private ItemRequestServiceImpl requestService;

    @Mock
    private ItemRequestRepository repository;

    @Mock
    private UserService userService;

    @Mock
    private ItemService itemService;

    @Mock
    private ItemRequestMapper requestMapper;

    @Test
    public void addRequest() {
        ItemRequestDto requestDto = new ItemRequestDto(1L, "text", LocalDateTime.now(), List.of());
        ItemRequest request = new ItemRequest(1L, null, new User(), null);
        when(userService.getById(anyLong())).thenReturn(new UserDto());
        when(requestMapper.convertFromDto(any())).thenReturn(request);
        when(repository.save(any())).thenReturn(request);
        when(requestMapper.convertToDto(request)).thenReturn(requestDto);

        ItemRequestDto savedRequest = requestService.add(1L, requestDto);
        assertThat(savedRequest).usingRecursiveComparison().isEqualTo(requestDto);
        verify(repository, times(1)).save(any());
    }

    @Test
    public void addRequestWithoutUserExist() {
        when(userService.getById(anyLong())).thenThrow(ObjectNotFoundException.class);

        assertThatThrownBy(() ->
                requestService.add(1L, new ItemRequestDto()))
                .isInstanceOf(ObjectNotFoundException.class);
    }

    @Test
    public void getAllByRequester() {
        ItemRequestDto requestDto = new ItemRequestDto(1L, "text", LocalDateTime.now(), List.of());
        ItemRequest request = new ItemRequest(1L, "text", new User(), LocalDateTime.now());
        ItemDto item = new ItemDto(1L, "item", "desc", true, 1L);
        ItemDto item2 = new ItemDto(2L, "item2", "desc2", true, 1L);
        when(userService.getById(anyLong())).thenReturn(new UserDto());
        when(repository.findAllByRequesterId(any(), any())).thenReturn(List.of(request));
        when(requestMapper.convertToDto(request)).thenReturn(requestDto);
        when(itemService.getAllByRequestId(anyLong())).thenReturn(List.of(item, item2));

        List<ItemRequestDto> requests = requestService.getAllByRequester(1L);
        assertThat(requests).hasSize(1);
        assertThat(requests.get(0)).usingRecursiveComparison().isEqualTo(requestDto);
        verify(repository, times(1)).findAllByRequesterId(any(), any());
    }

    @Test
    public void getAllByRequesterWithoutUserExist() {
        when(userService.getById(anyLong())).thenThrow(ObjectNotFoundException.class);

        assertThatThrownBy(() ->
                requestService.getAllByRequester(1L))
                .isInstanceOf(ObjectNotFoundException.class);
    }

    @Test
    public void getAllExceptRequester() {
        ItemRequestDto requestDto = new ItemRequestDto(1L, "text", LocalDateTime.now(), List.of());
        ItemRequest request = new ItemRequest(1L, "text", new User(), LocalDateTime.now());
        ItemDto item = new ItemDto(1L, "item", "desc", true, 1L);
        ItemDto item2 = new ItemDto(2L, "item2", "desc2", true, 1L);
        when(userService.getById(anyLong())).thenReturn(new UserDto());
        when(repository.findAllByRequesterIdNot(any(), any())).thenReturn(List.of(request));
        when(requestMapper.convertToDto(request)).thenReturn(requestDto);
        when(itemService.getAllByRequestId(anyLong())).thenReturn(List.of(item, item2));

        List<ItemRequestDto> requests = requestService.getAllExceptRequester(1L, 0, 10);
        assertThat(requests).hasSize(1);
        assertThat(requests.get(0)).usingRecursiveComparison().isEqualTo(requestDto);
        verify(repository, times(1)).findAllByRequesterIdNot(any(), any());
    }

    @Test
    public void getAllExceptRequesterWithoutUserExist() {
        when(userService.getById(anyLong())).thenThrow(ObjectNotFoundException.class);

        assertThatThrownBy(() ->
                requestService.getAllExceptRequester(1L, 0, 10))
                .isInstanceOf(ObjectNotFoundException.class);
    }

    @Test
    public void getById() {
        ItemRequestDto requestDto = new ItemRequestDto(1L, "text", LocalDateTime.now(), List.of());
        ItemDto item = new ItemDto(1L, "item", "desc", true, 1L);
        ItemDto item2 = new ItemDto(2L, "item2", "desc2", true, 1L);
        ItemRequest request = new ItemRequest();
        when(userService.getById(anyLong())).thenReturn(new UserDto());
        when(repository.findById(anyLong())).thenReturn(Optional.of(request));
        when(requestMapper.convertToDto(request)).thenReturn(requestDto);
        when(itemService.getAllByRequestId(anyLong())).thenReturn(List.of(item, item2));

        ItemRequestDto savedRequest = requestService.getById(1L, 1L);
        assertThat(savedRequest).usingRecursiveComparison().isEqualTo(requestDto);
        verify(repository, times(1)).findById(any());
    }

    @Test
    public void getByIdWithoutUserExist() {
        when(userService.getById(anyLong())).thenReturn(null);

        assertThatThrownBy(() ->
                requestService.getById(1L, 1L))
                .isInstanceOf(ObjectNotFoundException.class);
    }

    @Test
    public void getByIdWithoutRequestExist() {
        when(userService.getById(anyLong())).thenReturn(new UserDto());
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                requestService.getById(1L, 1L))
                .isInstanceOf(ObjectNotFoundException.class);
    }
}
