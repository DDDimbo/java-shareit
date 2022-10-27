package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.utility.FromSizeRequest;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class ItemRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRepository itemRepository;

    private User owner;

    private Item item;

    @Test
    public void contextLoads() {
        assertNotNull(em);
    }

    @BeforeEach
    public void beforeEach() {
        owner = User.builder()
                .name("John")
                .email("some@email.com")
                .build();
        em.persist(owner);
        item = Item.builder()
                .name("Аккумуляторная дрель")
                .description("Аккумуляторная дрель")
                .available(true)
                .owner(owner)
                .requestId(null)
                .build();
        em.persist(item);
    }

    @AfterEach
    public void afterEach() {
        em.clear();
    }

    @Test
    public void itemSearchTest() {
        final Pageable pageable = FromSizeRequest.of(0, 10);


        final var result = itemRepository.itemSearch("аккум", pageable);

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(item.getId()));
    }

    @Test
    public void itemSearchEmptyTest() {
        final Pageable pageable = FromSizeRequest.of(0, 10);

        final var result = itemRepository.itemSearch("акум", pageable);

        assertThat(result.size(), equalTo(0));
    }

    @Test
    public void findAllByRequestId() {
        final User user = User.builder()
                .name("John")
                .email("any@email.com")
                .build();
        em.persist(user);
        final ItemRequest itemRequest = ItemRequest.builder()
                .description("some text")
                .requesterId(user.getId())
                .created(LocalDateTime.now())
                .build();
        em.persist(itemRequest);
        final Item answer = Item.builder()
                .name("Аккумуляторная дрель")
                .description("Аккумуляторная дрель")
                .available(true)
                .owner(owner)
                .requestId(itemRequest.getId())
                .build();
        em.persist(answer);

        final var result = itemRepository.findAllByRequestId(itemRequest.getId());

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(answer.getId()));
    }

    @Test
    public void findAllByOwnerIdOrderByIdAsc() {
        final Pageable pageable = FromSizeRequest.of(0, 10);

        final var result = itemRepository.findAllByOwnerIdOrderByIdAsc(owner.getId(), pageable);

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(item.getId()));
    }
}
