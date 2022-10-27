package ru.practicum.shareit.request;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.utility.FromSizeRequest;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User requester;

    private ItemRequest itemRequest;

    @Test
    public void contextLoads() {
        assertNotNull(em);
    }

    @BeforeEach
    public void beforeEach() {
        requester = User.builder()
                .name("John")
                .email("some@email.com")
                .build();
        em.persist(requester);
        itemRequest = ItemRequest.builder()
                .description("some text")
                .requesterId(requester.getId())
                .created(LocalDateTime.now())
                .build();
        em.persist(itemRequest);
    }

    @AfterEach
    public void afterEach() {
        em.clear();
    }

    @Test
    public void findAllByRequesterIdIsOrderByCreatedDesc() {
        final var result = itemRequestRepository.findAllByRequesterIdIsOrderByCreatedDesc(requester.getId());

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(itemRequest.getId()));
    }

    @Test
    public void findAllByRequesterIdNotOrderByCreatedDesc() {
        final User newRequester = User.builder()
                .name("John")
                .email("any@email.com")
                .build();
        em.persist(newRequester);
        final ItemRequest newItemRequest = ItemRequest.builder()
                .description("some text")
                .requesterId(newRequester.getId())
                .created(LocalDateTime.now())
                .build();
        em.persist(newItemRequest);
        Pageable pageable = FromSizeRequest.of(0, 10);

        final var result = itemRequestRepository.findAllByRequesterIdNotOrderByCreatedDesc(newRequester.getId(), pageable);

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(itemRequest.getId()));
    }
}