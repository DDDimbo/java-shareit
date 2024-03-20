package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.CommentPrintView;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemFullPrintDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.utility.FromSizeRequest;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    private ItemService itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    private final ItemDto itemDtoWithAnswer = new ItemDto(2L, "Щётка для обуви",
            "Стандартная щётка для обуви", true, 1L);


    private final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("Щётка для машины")
            .description("Стандартная щётка для машины")
            .available(true)
            .build();


    private final ItemFullPrintDto itemFullPrintDto = ItemFullPrintDto.builder()
            .id(1L)
            .name("Щётка для машины")
            .description("Стандартная щётка для машины")
            .available(true)
            .lastBooking(null)
            .nextBooking(null)
            .comments(List.of())
            .build();

    private final User owner = new User(2L, "name", "name@user.com");

    private final Item item = new Item(1L, "Аккумуляторная дрель",
            "Аккумуляторная дрель", true, owner, null);


    @BeforeEach
    void setUp() {
        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository, commentRepository);
    }

    //    @AfterEach
//    void tearDown() {
//        verifyNoMoreInteractions(
//                userRepository
//        );
//
//    }


    @Test
    void findById() {
        final Long userId = 2L;
        final Long itemId = 1L;
        final List<CommentPrintView> comments = List.of();

        when(userRepository.existsById(userId))
                .thenReturn(true);
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));
        when(commentRepository.findFirst10ByItemIdOrderByCreatedDesc(itemId))
                .thenReturn(comments);
        when(bookingRepository.findFirstLastBooking(itemId))
                .thenReturn(Optional.empty());
        when(bookingRepository.findFirstNextBooking(itemId))
                .thenReturn(Optional.empty());

        final var result = itemService.findById(userId, itemId);

        assertThat(result.getId(), equalTo(item.getId()));
        assertThat(result.getDescription(), equalTo(item.getDescription()));
        assertThat(result.getName(), equalTo(item.getName()));
        assertThat(result.getAvailable(), equalTo(item.getAvailable()));
        assertThat(result.getLastBooking(), equalTo(null));
        assertThat(result.getNextBooking(), equalTo(null));
        assertThat(result.getComments().size(), equalTo(0));

        verify(userRepository, times(1))
                .existsById(userId);
        verify(itemRepository, times(1))
                .findById(itemId);
        verify(commentRepository, times(1))
                .findFirst10ByItemIdOrderByCreatedDesc(itemId);
        verify(bookingRepository, times(1))
                .findFirstLastBooking(itemId);
        verify(bookingRepository, times(1))
                .findFirstNextBooking(itemId);
    }

    @Test
    void findByIdByIdUserNotFoundExceptionTest() {
        final Long userId = 2L;
        final Long itemId = 1L;

        when(userRepository.existsById(userId))
                .thenReturn(true);
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.empty());

        final var exception = assertThrows(
                ItemNotFoundException.class,
                () -> itemService.findById(userId, itemId)
        );

        assertThat("Item с идентификатором " + itemId + " не найден.", equalTo(exception.getMessage()));

        verify(userRepository, times(1))
                .existsById(userId);
        verify(userRepository, times(1))
                .existsById(userId);
    }

    @Test
    void findByIdByIdItemNotFoundExceptionTest() {
        final Long userId = 2L;
        final Long itemId = 1L;

        when(userRepository.existsById(userId))
                .thenReturn(false);

        final var exception = assertThrows(
                UserNotFoundException.class,
                () -> itemService.findById(userId, itemId)
        );

        assertThat("Пользователя с таким id не существует", equalTo(exception.getMessage()));

        verify(userRepository, times(1))
                .existsById(userId);
    }

    @Test
    void findAll() {
    }

    @Test
    void deleteById() {
        final Long id = 1L;

        doNothing()
                .when(itemRepository).deleteById(id);

        itemService.deleteById(id);

        verify(itemRepository, times(1))
                .deleteById(id);
    }

    @Test
    void search() {
        final String text = "text";
        final int from = 0;
        final int size = 10;
        Pageable pageable = FromSizeRequest.of(from, size);
        List<Item> items = List.of(item);

        when(itemRepository.itemSearch(text, pageable))
                .thenReturn(items);

        final var result = itemService.search(text, from, size);

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(item.getId()));
        assertThat(result.get(0).getName(), equalTo(item.getName()));
        assertThat(result.get(0).getDescription(), equalTo(item.getDescription()));
        assertThat(result.get(0).getAvailable(), equalTo(item.getAvailable()));
        assertThat(result.get(0).getRequestId(), equalTo(item.getRequestId()));

        verify(itemRepository, times(1))
                .itemSearch(text, pageable);
    }
}