package ru.practicum.shareit.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void contextLoads() {
        assertNotNull(em);
    }

    @AfterEach
    public void afterEach() {
        em.clear();
    }

    // Наставник сказал, что переопределенные методы не нужно тестить
    @Test
    void existsByEmail() {
        final User user = User.builder()
                .name("John")
                .email("some@email.com")
                .build();
        em.persist(user);

        boolean result = userRepository.existsByEmail("some@email.com");
        assertTrue(result);
    }

}