package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    public void contextLoads() {
        assertNotNull(em);
    }

    @AfterEach
    public void afterEach() {
        em.clear();
    }

    @Test
    void findFirst10ByItemIdOrderByCreatedDescTest() {
        final User owner = User.builder()
                .name("John")
                .email("some@email.com")
                .build();
        em.persist(owner);
        final Item item = Item.builder()
                .name("Аккумуляторная дрель")
                .description("Аккумуляторная дрель")
                .available(true)
                .owner(owner)
                .build();
        em.persist(item);
        final User author = User.builder()
                .name("John")
                .email("hfkg@email.com")
                .build();
        em.persist(author);
        final Comment comment = Comment.builder()
                .text("some comment")
                .author(author)
                .itemId(item.getId())
                .created(LocalDateTime.now())
                .build();
        em.persist(comment);

        final var result = commentRepository.findFirst10ByItemIdOrderByCreatedDesc(item.getId());
        assertThat(result.size(), equalTo(1));
    }

}