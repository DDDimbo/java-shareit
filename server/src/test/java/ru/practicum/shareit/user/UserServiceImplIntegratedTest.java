package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.AlreadyExistsEmailException;
import ru.practicum.shareit.exceptions.UserNotFoundException;

import javax.persistence.EntityManager;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImplIntegratedTest {

    private final EntityManager em;
    private final UserService userService;

    private final UserRepository userRepository;

    private User create;

    @BeforeEach
    void beforeEach() {
        create = User.builder()
                .name("John")
                .email("some@email.com")
                .build();
        em.persist(create);
    }


    @Test
    void saveUser() {
        final UserDto userDto = UserDto.builder()
                .name("John2")
                .email("some2@email.com")
                .build();
        em.persist(create);

        UserDto resUserDto = userService.create(userDto);
        User resUser = userRepository.findById(resUserDto.getId()).get();

        assertThat(resUser.getId(), equalTo(resUserDto.getId()));
        assertThat(resUser.getName(), equalTo(resUserDto.getName()));
        assertThat(resUser.getEmail(), equalTo(resUserDto.getEmail()));
    }

    @Test
    void updateUserEmailAndName() {
        // given
        final Long id = create.getId();
        final UserDto upUserDto = UserDto.builder()
                .name("newName")
                .email("new@email.com")
                .build();

        // when
        userService.update(id, upUserDto);

        // then
        User resUser = userRepository.findById(id).get();

        assertThat(resUser.getId(), equalTo(id));
        assertThat(resUser.getName(), equalTo("newName"));
        assertThat(resUser.getEmail(), equalTo("new@email.com"));
    }

    @Test
    void updateUserName() {
        // given
        final Long id = create.getId();
        final UserDto upUserDto = UserDto.builder()
                .name("newName")
                .build();

        // when
        userService.update(id, upUserDto);

        // then
        User resUser = userRepository.findById(id).get();

        assertThat(resUser.getId(), equalTo(id));
        assertThat(resUser.getName(), equalTo("newName"));
        assertThat(resUser.getEmail(), equalTo("some@email.com"));
    }

    @Test
    void updateUserEmail() {
        // given
        final Long id = create.getId();
        final UserDto upUserDto = UserDto.builder()
                .email("new@email.com")
                .build();

        // when
        userService.update(id, upUserDto);

        // then
        User resUser = userRepository.findById(id).get();

        assertThat(resUser.getId(), equalTo(id));
        assertThat(resUser.getName(), equalTo("John"));
        assertThat(resUser.getEmail(), equalTo("new@email.com"));
    }

    @Test
    void updateUserNotFoundException() {
        // given
        final Long id = create.getId();
        userService.deleteById(id);
        final UserDto userDto = UserDto.builder()
                .name("John")
                .email("some@email.com")
                .build();

        // when
        final var exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.update(id, userDto)
        );

        assertThat("User с идентификатором " + id + " не найден.", equalTo(exception.getMessage()));
    }

    @Test
    void updateAlreadyExistsEmailException() {
        // given
        final Long id = create.getId();
        final UserDto userDto = UserDto.builder()
                .name("John")
                .email("some@email.com")
                .build();

        // when
        final var exception = assertThrows(
                AlreadyExistsEmailException.class,
                () -> userService.update(id, userDto)
        );

        assertThat("Новый email совпадает со старым", equalTo(exception.getMessage()));
    }

}
