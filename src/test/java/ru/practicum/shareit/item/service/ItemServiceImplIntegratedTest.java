package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.exceptions.AccessBookingException;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.OwnerAccessException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemFullPrintDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImplIntegratedTest {

    private final EntityManager em;

    private final ItemService itemService;

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    private User createOwner;

    private Item createItem;

    @BeforeEach
    void beforeEach() {
        createOwner = User.builder()
                .name("John")
                .email("some@email.com")
                .build();
        userRepository.save(createOwner);
        createItem = Item.builder()
                .name("Аккумуляторная дрель")
                .description("Аккумуляторная дрель")
                .available(true)
                .owner(createOwner)
                .build();
        itemRepository.save(createItem);
    }

    @Test
    void createItem() {
        final ItemDto itemDto = ItemDto.builder()
                .name("Дрель")
                .description("Дрель")
                .available(true)
                .build();

        final ItemDto resItemDto = itemService.create(createOwner.getId(), itemDto);
        final Item resItem = itemRepository.findById(resItemDto.getId()).get();

        assertThat(resItem.getId(), equalTo(resItemDto.getId()));
        assertThat(resItem.getName(), equalTo(resItemDto.getName()));
        assertThat(resItem.getDescription(), equalTo(resItemDto.getDescription()));
        assertThat(resItem.getAvailable(), equalTo(resItemDto.getAvailable()));
        assertThat(resItem.getOwner(), equalTo(createOwner));
    }

    @Test
    void createItemUserNotFoundException() {
        final Long userId = createOwner.getId();
        final ItemDto itemDto = ItemDto.builder()
                .name("Дрель")
                .description("Дрель")
                .available(true)
                .build();
        userRepository.deleteById(userId);

        final var exception = assertThrows(
                UserNotFoundException.class,
                () -> itemService.create(userId, itemDto)
        );

        assertThat("Пользователя с таким id не существует", equalTo(exception.getMessage()));
    }

    @Test
    void createComment() {
        final User author = User.builder()
                .name("John")
                .email("hfkg@email.com")
                .build();
        userRepository.save(author);
        final Booking booking = Booking.builder()
                .start(LocalDateTime.now().minusDays(3))
                .end(LocalDateTime.now().minusDays(1))
                .item(createItem)
                .booker(author)
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking);
        final CommentDto commentdto = CommentDto.builder()
                .text("some comment")
                .build();

        final var resCommentDto = itemService.createComment(author.getId(), createItem.getId(), commentdto);
        final var resComment = commentRepository.findById(resCommentDto.getId()).get();

        assertThat(resCommentDto.getId(), equalTo(resComment.getId()));
        assertThat(resCommentDto.getText(), equalTo(resComment.getText()));
        assertThat(resCommentDto.getItemId(), equalTo(resComment.getItemId()));
        assertThat(resCommentDto.getAuthorName(), equalTo(resComment.getAuthor().getName()));
    }

    @Test
    void createCommentItemNotFoundExceptionTest() {
        final Long userId = createOwner.getId();
        final Long itemId = createItem.getId();
        itemRepository.deleteById(itemId);
        final User author = User.builder()
                .name("John")
                .email("hfkg@email.com")
                .build();
        userRepository.save(author);
        final Booking booking = Booking.builder()
                .start(LocalDateTime.now().minusDays(3))
                .end(LocalDateTime.now().minusDays(1))
                .item(createItem)
                .booker(author)
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking);
        final CommentDto commentdto = CommentDto.builder()
                .text("some comment")
                .build();

        final var exception = assertThrows(
                ItemNotFoundException.class,
                () -> itemService.createComment(userId, itemId, commentdto)
        );

        assertThat("Вещи с таким id не существует", equalTo(exception.getMessage()));
    }

    @Test
    void createCommentAccessBookingExceptionTest() {
        final Long userId = createOwner.getId();
        final Long itemId = createItem.getId();
        final User author = User.builder()
                .name("John")
                .email("hfkg@email.com")
                .build();
        userRepository.save(author);
        final Booking booking = Booking.builder()
                .start(LocalDateTime.now().minusDays(3))
                .end(LocalDateTime.now().minusDays(1))
                .item(createItem)
                .booker(author)
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking);
        final CommentDto commentdto = CommentDto.builder()
                .text("some comment")
                .build();

        final var exception = assertThrows(
                AccessBookingException.class,
                () -> itemService.createComment(userId, itemId, commentdto)
        );

        assertThat("Пользователь, который не брал в аренду вещь, не может написать комментарий", equalTo(exception.getMessage()));
    }

    @Test
    void findALlTest() {
        final int from = 0;
        final int size = 10;
        final Long userId = createOwner.getId();
        final Long itemId = createItem.getId();

        final List<ItemFullPrintDto> result = itemService.findAll(userId, from, size);

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(itemId));
        assertThat(result.get(0).getName(), equalTo(createItem.getName()));
        assertThat(result.get(0).getDescription(), equalTo(createItem.getDescription()));
        assertThat(result.get(0).getAvailable(), equalTo(createItem.getAvailable()));
        assertThat(result.get(0).getLastBooking(), equalTo(null));
        assertThat(result.get(0).getNextBooking(), equalTo(null));
        assertThat(result.get(0).getComments(), equalTo(List.of()));
    }



    @Test
    void updateTest() {
        final Long ownerId = createOwner.getId();
        final Long itemId = createItem.getId();
        ItemDto itemDto = ItemDto.builder()
                .name("newName")
                .description("NewDescription")
                .available(true)
                .build();

        ItemDto resItemDto = itemService.update(ownerId, itemId, itemDto);

        assertThat(resItemDto.getId(), equalTo(createItem.getId()));
        assertThat(resItemDto.getDescription(), equalTo(createItem.getDescription()));
        assertThat(resItemDto.getName(), equalTo(createItem.getName()));
        assertThat(resItemDto.getAvailable(), equalTo(createItem.getAvailable()));
        assertThat(resItemDto.getRequestId(), equalTo(null));
    }

    @Test
    void updateNameAndDescriptionTest() {
        final Long ownerId = createOwner.getId();
        final Long itemId = createItem.getId();
        ItemDto itemDto = ItemDto.builder()
                .name("newName")
                .description("NewDescription")
                .build();

        ItemDto resItemDto = itemService.update(ownerId, itemId, itemDto);

        assertThat(resItemDto.getId(), equalTo(createItem.getId()));
        assertThat(resItemDto.getDescription(), equalTo(createItem.getDescription()));
        assertThat(resItemDto.getName(), equalTo(createItem.getName()));
        assertThat(resItemDto.getAvailable(), equalTo(createItem.getAvailable()));
        assertThat(resItemDto.getRequestId(), equalTo(null));
    }

    @Test
    void updateNameTest() {
        final Long ownerId = createOwner.getId();
        final Long itemId = createItem.getId();
        ItemDto itemDto = ItemDto.builder()
                .name("newName")
                .build();

        ItemDto resItemDto = itemService.update(ownerId, itemId, itemDto);

        assertThat(resItemDto.getId(), equalTo(createItem.getId()));
        assertThat(resItemDto.getDescription(), equalTo(createItem.getDescription()));
        assertThat(resItemDto.getName(), equalTo(createItem.getName()));
        assertThat(resItemDto.getAvailable(), equalTo(createItem.getAvailable()));
        assertThat(resItemDto.getRequestId(), equalTo(null));
    }

    @Test
    void updateDescriptionTest() {
        final Long ownerId = createOwner.getId();
        final Long itemId = createItem.getId();
        ItemDto itemDto = ItemDto.builder()
                .description("NewDescription")
                .build();

        ItemDto resItemDto = itemService.update(ownerId, itemId, itemDto);

        assertThat(resItemDto.getId(), equalTo(createItem.getId()));
        assertThat(resItemDto.getDescription(), equalTo(createItem.getDescription()));
        assertThat(resItemDto.getName(), equalTo(createItem.getName()));
        assertThat(resItemDto.getAvailable(), equalTo(createItem.getAvailable()));
        assertThat(resItemDto.getRequestId(), equalTo(null));
    }

    @Test
    void updateTestAvailable() {
        final Long ownerId = createOwner.getId();
        final Long itemId = createItem.getId();
        ItemDto itemDto = ItemDto.builder()
                .available(true)
                .build();

        ItemDto resItemDto = itemService.update(ownerId, itemId, itemDto);

        assertThat(resItemDto.getId(), equalTo(createItem.getId()));
        assertThat(resItemDto.getDescription(), equalTo(createItem.getDescription()));
        assertThat(resItemDto.getName(), equalTo(createItem.getName()));
        assertThat(resItemDto.getAvailable(), equalTo(createItem.getAvailable()));
        assertThat(resItemDto.getRequestId(), equalTo(null));
    }

    @Test
    void updateUserNotFoundExceptionTest() {
        final Long ownerId = createOwner.getId();
        final Long itemId = createItem.getId();
        userRepository.deleteById(ownerId);
        ItemDto itemDto = ItemDto.builder()
                .name("newName")
                .description("NewDescription")
                .available(true)
                .build();

        final var exception = assertThrows(
                UserNotFoundException.class,
                () -> itemService.update(ownerId, itemId, itemDto)
        );

        assertThat("Пользователя с таким id не существует", equalTo(exception.getMessage()));
    }

    @Test
    void updateOwnerAccessException() {
        final User tempUser = User.builder()
                .name("Terry")
                .email("hfgffhg@email.com")
                .build();
        userRepository.save(tempUser);
        final Long itemId = createItem.getId();
        ItemDto itemDto = ItemDto.builder()
                .name("newName")
                .description("NewDescription")
                .available(true)
                .build();

        final var exception = assertThrows(
                OwnerAccessException.class,
                () -> itemService.update(tempUser.getId(), itemId, itemDto)
        );

        assertThat("Данный пользователь не является владельцем вещи", equalTo(exception.getMessage()));
    }

}
