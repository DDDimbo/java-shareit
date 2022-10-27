package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exceptions.ItemRequestNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.utility.FromSizeRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    private ItemRequestService itemRequestService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    private final static LocalDateTime time = LocalDateTime.now();
    private final ItemRequestDto itemRequestDto = ItemRequestDto.builder()
            .id(1L)
            .description("description")
            .requesterId(1L)
            .created(time)
            .items(List.of())
            .build();


    @BeforeEach
    void setUp() {
        itemRequestService = new ItemRequestServiceImpl(userRepository, itemRepository, itemRequestRepository);
    }

    //    @AfterEach
//    void tearDown() {
//        verifyNoMoreInteractions(
//                userRepository
//        );
//
//    }

    @Test
    void createUserNotFoundExceptionExistsTest() {
        final Long userId = 1L;
        final ItemRequestDto createDto = ItemRequestDto.builder()
                .description("description")
                .requesterId(1L)
                .created(time)
                .build();

        when(userRepository.existsById(userId))
                .thenThrow(new UserNotFoundException("Пользователя с таким id не существует"));

        final var exception = assertThrows(
                UserNotFoundException.class,
                () -> itemRequestService.create(userId, createDto)
        );

        assertThat("Пользователя с таким id не существует", equalTo(exception.getMessage()));

        verify(userRepository, times(1))
                .existsById(userId);
    }

    @Test
    void findAllByRequester() {
    }

    @Test
    void findAll() {
        final Long userId = 1L;
        final int from = 0;
        final int size = 10;
        Pageable pageable = FromSizeRequest.of(from, size);
        final ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("description")
                .requesterId(1L)
                .created(time)
                .build();
        List<ItemRequest> itemRequestList = List.of(itemRequest);


        when(userRepository.existsById(userId))
                .thenReturn(true);
        when(itemRequestRepository.findAllByRequesterIdNotOrderByCreatedDesc(userId, pageable))
                .thenReturn(itemRequestList);
        when(itemRepository.findAllByRequestId(1L))
                .thenReturn(List.of());

        final var result = itemRequestService.findAll(userId, from, size);

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(itemRequestDto.getId()));
        assertThat(result.get(0).getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(result.get(0).getRequesterId(), equalTo(itemRequestDto.getRequesterId()));
        assertThat(result.get(0).getCreated(), equalTo(itemRequestDto.getCreated()));
        assertThat(result.get(0).getItems().size(), equalTo(0));

        verify(userRepository, times(1))
                .existsById(userId);
        verify(itemRequestRepository, times(1))
                .findAllByRequesterIdNotOrderByCreatedDesc(userId, pageable);
        verify(itemRepository, times(1))
                .findAllByRequestId(1L);
    }

    @Test
    void findAllUserNotFoundExceptionExistsTest() {
        final Long userId = 1L;
        final Integer from = 0;
        final Integer size = 10;

        when(userRepository.existsById(userId))
                .thenReturn(false);

        final var exception = assertThrows(
                UserNotFoundException.class,
                () -> itemRequestService.findAll(userId, from, size)
        );

        assertThat("Пользователя с таким id не существует", equalTo(exception.getMessage()));

        verify(userRepository, times(1))
                .existsById(userId);
    }

    @Test
    void findById() {
        final Long userId = 1L;
        final Long requestId = 1L;
        final ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("description")
                .requesterId(1L)
                .created(time)
                .build();

        when(userRepository.existsById(userId))
                .thenReturn(true);
        when(itemRequestRepository.existsById(requestId))
                .thenReturn(true);
        when(itemRequestRepository.findById(requestId))
                .thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAllByRequestId(requestId))
                .thenReturn(List.of());

        final var result = itemRequestService.findById(userId, requestId);

        assertThat(result.getId(), equalTo(itemRequestDto.getId()));
        assertThat(result.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(result.getRequesterId(), equalTo(itemRequestDto.getRequesterId()));
        assertThat(result.getCreated(), equalTo(itemRequestDto.getCreated()));
        assertThat(result.getItems(), equalTo(itemRequestDto.getItems()));

        verify(userRepository, times(1))
                .existsById(userId);
        verify(itemRequestRepository, times(1))
                .existsById(requestId);
        verify(itemRequestRepository, times(1))
                .findById(requestId);
        verify(itemRepository, times(1))
                .findAllByRequestId(requestId);

    }

    @Test
    void findByIdItemRequestNotFoundExceptionExistsTest() {
        final Long userId = 1L;
        final Long requestId = 1L;

        when(userRepository.existsById(userId))
                .thenReturn(true);
        when(itemRequestRepository.existsById(requestId))
                .thenReturn(false);

        final var exception = assertThrows(
                ItemRequestNotFoundException.class,
                () -> itemRequestService.findById(userId, requestId)
        );

        assertThat("Запроса с таким id не существует", equalTo(exception.getMessage()));

        verify(userRepository, times(1))
                .existsById(userId);
        verify(itemRequestRepository, times(1))
                .existsById(requestId);
    }

    @Test
    void findByIdUserNotFoundExceptionExistsTest() {
        final Long userId = 1L;
        final Long requestId = 1L;

        when(userRepository.existsById(userId))
                .thenReturn(false);

        final var exception = assertThrows(
                UserNotFoundException.class,
                () -> itemRequestService.findById(userId, requestId)
        );

        assertThat("Пользователя с таким id не существует", equalTo(exception.getMessage()));

        verify(userRepository, times(1))
                .existsById(userId);
    }

    @Test
    void findByIdItemRequestNotFoundExceptionFindByIdTest() {
        final Long userId = 1L;
        final Long requestId = 1L;

        when(userRepository.existsById(userId))
                .thenReturn(true);
        when(itemRequestRepository.existsById(requestId))
                .thenReturn(true);
        when(itemRequestRepository.findById(requestId))
                .thenReturn(Optional.empty());

        final var exception = assertThrows(
                ItemRequestNotFoundException.class,
                () -> itemRequestService.findById(userId, requestId)
        );

        assertThat("Запроса с " + requestId + " не существует", equalTo(exception.getMessage()));

        verify(userRepository, times(1))
                .existsById(userId);
        verify(itemRequestRepository, times(1))
                .existsById(requestId);
        verify(itemRequestRepository, times(1))
                .findById(requestId);
    }
}