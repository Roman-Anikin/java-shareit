package shareit.app.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import shareit.app.booking.BookingService;
import shareit.app.booking.BookingStatus;
import shareit.app.booking.dto.BookingDto;
import shareit.app.exception.ObjectNotFoundException;
import shareit.app.item.dto.CommentDto;
import shareit.app.item.dto.ItemDto;
import shareit.app.item.dto.OwnerItemDto;
import shareit.app.user.User;
import shareit.app.user.UserService;
import shareit.app.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private UserService userService;

    @Mock
    private CommentService commentService;

    @Mock
    private BookingService bookingService;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private OwnerItemMapper ownerItemMapper;

    @Mock
    private ItemRepository repository;

    @Test
    public void addItem() {
        ItemDto itemDto = new ItemDto(1L, "item", "desc", true, null);
        Item item = new Item(1L, "item", "desc", true, new User(), null);
        when(userService.getById(anyLong())).thenReturn(new UserDto());
        when(itemMapper.convertFromDto(itemDto)).thenReturn(item);
        when(repository.save(any())).thenReturn(item);
        when(itemMapper.convertToDto(item)).thenReturn(itemDto);

        ItemDto savedItem = itemService.add(1L, itemDto);
        assertThat(savedItem).usingRecursiveComparison().isEqualTo(itemDto);
    }

    @Test
    public void addItemWithoutOwner() {
        when(userService.getById(anyLong())).thenThrow(ObjectNotFoundException.class);

        assertThatThrownBy(() ->
                itemService.add(1L, new ItemDto()))
                .isInstanceOf(ObjectNotFoundException.class);
    }

    @Test
    public void updateItem() {
        ItemDto itemDto = new ItemDto(1L, "new item", "new desc", false, null);
        Item item = new Item(1L, "item", "desc", true, new User(), null);
        when(userService.getById(anyLong())).thenReturn(new UserDto());
        when(repository.getByIdAndOwnerId(any(), any())).thenReturn(Optional.of(item));
        when(itemMapper.convertToDto(item)).thenReturn(itemDto);

        ItemDto savedItem = itemService.update(itemDto.getId(), 1L, itemDto);
        assertThat(savedItem).usingRecursiveComparison().isEqualTo(itemDto);
    }

    @Test
    public void updateItemWithOnlyName() {
        ItemDto itemDto = new ItemDto(1L, "new item", null, null, null);
        Item item = new Item(1L, "item", "desc", true, new User(), null);
        when(userService.getById(anyLong())).thenReturn(new UserDto());
        when(repository.getByIdAndOwnerId(any(), any())).thenReturn(Optional.of(item));
        when(itemMapper.convertToDto(item)).thenReturn(new ItemDto(1L, "new item", "desc",
                true, null));

        ItemDto savedItem = itemService.update(itemDto.getId(), 1L, itemDto);
        assertThat(savedItem.getId()).isEqualTo(itemDto.getId());
        assertThat(savedItem.getName()).isEqualTo(itemDto.getName());
        assertThat(savedItem.getDescription()).isEqualTo(item.getDescription());
        assertThat(savedItem.getAvailable()).isEqualTo(item.getAvailable());
    }

    @Test
    public void updateItemWithOnlyDescription() {
        ItemDto itemDto = new ItemDto(1L, null, "new desc", null, null);
        Item item = new Item(1L, "item", "desc", true, new User(), null);
        when(userService.getById(anyLong())).thenReturn(new UserDto());
        when(repository.getByIdAndOwnerId(any(), any())).thenReturn(Optional.of(item));
        when(itemMapper.convertToDto(item)).thenReturn(new ItemDto(1L, "item", "new desc",
                true, null));

        ItemDto savedItem = itemService.update(itemDto.getId(), 1L, itemDto);
        assertThat(savedItem.getId()).isEqualTo(itemDto.getId());
        assertThat(savedItem.getName()).isEqualTo(item.getName());
        assertThat(savedItem.getDescription()).isEqualTo(itemDto.getDescription());
        assertThat(savedItem.getAvailable()).isEqualTo(item.getAvailable());
    }

    @Test
    public void updateItemWithOnlyAvailable() {
        ItemDto itemDto = new ItemDto(1L, null, null, false, null);
        Item item = new Item(1L, "item", "desc", true, new User(), null);
        when(userService.getById(anyLong())).thenReturn(new UserDto());
        when(repository.getByIdAndOwnerId(any(), any())).thenReturn(Optional.of(item));
        when(itemMapper.convertToDto(item)).thenReturn(new ItemDto(1L, "item", "desc",
                false, null));

        ItemDto savedItem = itemService.update(itemDto.getId(), 1L, itemDto);
        assertThat(savedItem.getId()).isEqualTo(itemDto.getId());
        assertThat(savedItem.getName()).isEqualTo(item.getName());
        assertThat(savedItem.getDescription()).isEqualTo(item.getDescription());
        assertThat(savedItem.getAvailable()).isEqualTo(itemDto.getAvailable());
    }

    @Test
    public void updateItemByWrongOwner() {
        when(userService.getById(anyLong())).thenReturn(new UserDto());

        assertThatThrownBy(() ->
                itemService.update(1L, 1L, new ItemDto()))
                .isInstanceOf(ObjectNotFoundException.class);
    }

    @Test
    public void updateItemWithoutOwner() {
        ItemDto itemDto = new ItemDto(1L, "item", "desc", true, null);
        when(userService.getById(anyLong())).thenThrow(ObjectNotFoundException.class);

        assertThatThrownBy(() ->
                itemService.update(itemDto.getId(), 1L, itemDto))
                .isInstanceOf(ObjectNotFoundException.class);
    }

    @Test
    public void getByIdByUser() {
        OwnerItemDto itemDto = new OwnerItemDto(1L, "item", "desc", true, null,
                null, List.of(), null);
        Item item = new Item(1L, "item", "desc", true,
                new User(1L, "name", "desc"), null);
        when(repository.findById(anyLong())).thenReturn(Optional.of(item));
        when(ownerItemMapper.convertToDto(item)).thenReturn(itemDto);
        when(commentService.getAllByItemId(anyLong())).thenReturn(List.of());

        OwnerItemDto savedItem = itemService.getById(2L, itemDto.getId());
        assertThat(savedItem).usingRecursiveComparison().isEqualTo(itemDto);
    }

    @Test
    public void getByIdByOwner() {
        OwnerItemDto itemDto = new OwnerItemDto(1L, "item", "desc", true,
                null, null, List.of(), null);
        Item item = new Item(1L, "item", "desc", true,
                new User(1L, "name", "desc"), null);
        BookingDto lastBooking = new BookingDto(1L, null, null, item, 1L, new User(), 2L,
                BookingStatus.APPROVED);
        BookingDto nextBooking = new BookingDto(2L, null, null, item, 2L, new User(), 3L,
                BookingStatus.WAITING);
        when(repository.findById(anyLong())).thenReturn(Optional.of(item));
        when(ownerItemMapper.convertToDto(item)).thenReturn(itemDto);
        when(commentService.getAllByItemId(anyLong())).thenReturn(List.of());
        when(bookingService.getLastBooking(anyLong())).thenReturn(lastBooking);
        when(bookingService.getNextBooking(anyLong())).thenReturn(nextBooking);

        OwnerItemDto savedItem = itemService.getById(item.getOwner().getId(), itemDto.getId());
        assertThat(savedItem.getId()).isEqualTo(itemDto.getId());
        assertThat(savedItem.getName()).isEqualTo(itemDto.getName());
        assertThat(savedItem.getDescription()).isEqualTo(itemDto.getDescription());
        assertThat(savedItem.getAvailable()).isEqualTo(itemDto.getAvailable());
        assertThat(savedItem.getLastBooking().getId()).isEqualTo(lastBooking.getId());
        assertThat(savedItem.getNextBooking().getId()).isEqualTo(nextBooking.getId());
        assertThat(savedItem.getComments()).hasSize(0);
        assertThat(savedItem.getItemRequest()).isNull();
    }

    @Test
    public void getByIdByWrongUser() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                itemService.getById(1L, 1L))
                .isInstanceOf(ObjectNotFoundException.class);
    }

    @Test
    public void geAllByOwner() {
        OwnerItemDto itemDto = new OwnerItemDto(1L, "item", "desc", true,
                null, null, List.of(), null);
        OwnerItemDto itemDto2 = new OwnerItemDto(2L, "item2", "desc2", false,
                null, null, List.of(), null);
        Item item = new Item(1L, "item", "desc", true,
                new User(1L, "name", "desc"), null);
        Item item2 = new Item(2L, "item2", "desc2", true,
                new User(1L, "name", "desc"), null);
        BookingDto lastBooking = new BookingDto(1L, null, null, item, 1L, new User(), 2L,
                BookingStatus.APPROVED);
        BookingDto nextBooking = new BookingDto(2L, null, null, item, 1L, new User(), 3L,
                BookingStatus.WAITING);
        when(ownerItemMapper.convertToDto(List.of(item, item2))).thenReturn(List.of(itemDto, itemDto2));
        when(repository.getAllByOwnerId(anyLong(), any())).thenReturn(List.of(item, item2));
        when(bookingService.getLastBooking(item.getId())).thenReturn(lastBooking);
        when(bookingService.getNextBooking(item.getId())).thenReturn(nextBooking);

        List<OwnerItemDto> savedItems = itemService.getByOwner(item.getOwner().getId(), 0, 10);
        assertThat(savedItems.get(0).getId()).isEqualTo(itemDto.getId());
        assertThat(savedItems.get(0).getName()).isEqualTo(itemDto.getName());
        assertThat(savedItems.get(0).getDescription()).isEqualTo(itemDto.getDescription());
        assertThat(savedItems.get(0).getAvailable()).isEqualTo(itemDto.getAvailable());
        assertThat(savedItems.get(0).getLastBooking().getId()).isEqualTo(lastBooking.getId());
        assertThat(savedItems.get(0).getNextBooking().getId()).isEqualTo(nextBooking.getId());
        assertThat(savedItems.get(0).getComments()).hasSize(0);
        assertThat(savedItems.get(0).getItemRequest()).isNull();

        assertThat(savedItems.get(1)).usingRecursiveComparison().isEqualTo(itemDto2);
    }

    @Test
    public void geAllByWrongOwner() {
        when(ownerItemMapper.convertToDto(List.of())).thenReturn(List.of());
        when(repository.getAllByOwnerId(anyLong(), any())).thenReturn(List.of());

        List<OwnerItemDto> savedItems = itemService.getByOwner(1L, 0, 10);
        assertThat(savedItems).hasSize(0);
    }

    @Test
    public void searchByName() {
        ItemDto itemDto = new ItemDto(1L, "item", "desc", true, null);
        Item item = new Item(1L, "item", "desc", true, new User(), null);
        when(userService.getById(anyLong())).thenReturn(new UserDto());
        when(repository.getAllByText(anyString(), any())).thenReturn(List.of(item));
        when(itemMapper.convertToDto(anyList())).thenReturn(List.of(itemDto));

        List<ItemDto> items = itemService.searchByText(1L, "ItEm", 0, 10);
        assertThat(items).hasSize(1);
        assertThat(items.get(0)).usingRecursiveComparison().isEqualTo(itemDto);
    }

    @Test
    public void searchByDescription() {
        ItemDto itemDto = new ItemDto(1L, "item", "desc", true, null);
        Item item = new Item(1L, "item", "desc", true, new User(), null);
        when(userService.getById(anyLong())).thenReturn(new UserDto());
        when(repository.getAllByText(anyString(), any())).thenReturn(List.of(item));
        when(itemMapper.convertToDto(anyList())).thenReturn(List.of(itemDto));

        List<ItemDto> items = itemService.searchByText(1L, "DESC   ", 0, 10);
        assertThat(items).hasSize(1);
        assertThat(items.get(0)).usingRecursiveComparison().isEqualTo(itemDto);
    }

    @Test
    public void searchWithEmptyText() {
        when(userService.getById(anyLong())).thenReturn(new UserDto());
        when(itemMapper.convertToDto(anyList())).thenReturn(List.of());

        List<ItemDto> items = itemService.searchByText(1L, "   ", 0, 10);
        assertThat(items).hasSize(0);
    }

    @Test
    public void addComment() {
        CommentDto commentDto = new CommentDto(1L, "comment", "user", null);
        when(commentService.add(anyLong(), anyLong(), any())).thenReturn(commentDto);

        CommentDto savedComment = itemService.addComment(1L, 1L, commentDto);
        assertThat(savedComment).usingRecursiveComparison().isEqualTo(commentDto);
    }

    @Test
    public void getAllByRequest() {
        ItemDto itemDto = new ItemDto(1L, "item", "desc", true, 1L);
        Item item = new Item(1L, "item", "desc", true, new User(), null);
        when(repository.getAllByRequestId(1L)).thenReturn(List.of(item));
        when(itemMapper.convertToDto(List.of(item))).thenReturn(List.of(itemDto));

        List<ItemDto> savedItems = itemService.getAllByRequestId(1L);
        assertThat(savedItems).hasSize(1);
        assertThat(savedItems.get(0).getId()).isEqualTo(itemDto.getId());
    }

    @Test
    public void getAllWithoutRequests() {
        when(repository.getAllByRequestId(1L)).thenReturn(List.of());
        when(itemMapper.convertToDto(List.of())).thenReturn(List.of());

        List<ItemDto> savedItems = itemService.getAllByRequestId(1L);
        assertThat(savedItems).hasSize(0);
    }
}
