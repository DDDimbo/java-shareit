package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.UserNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
//@MockitoSettings(strictness = Strictness.WARN)
class UserServiceImplTest {

    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository);
    }

//    @AfterEach
//    void tearDown() {
//        verifyNoMoreInteractions(
//                userRepository
//        );
//
//    }

    private final UserDto userDto = UserDto.builder()
            .id(1L)
            .name("John")
            .email("john.doe@mail.com")
            .build();

    private final User user = User.builder()
            .id(1L)
            .name("John")
            .email("john.doe@mail.com")
            .build();

    private final UserDto userDtoCreate = UserDto.builder()
            .name("John")
            .email("john.doe@mail.com")
            .build();


    @Test
    void findByIdTest() {
        final Long id = 1L;

        when(userRepository.findById(id))
                .thenReturn(Optional.of(user));

        final var result = userService.findById(id);

        assertThat(result.getId(), equalTo(user.getId()));
        assertThat(result.getName(), equalTo(user.getName()));
        assertThat(result.getEmail(), equalTo(user.getEmail()));

        verify(userRepository, times(1))
                .findById(id);
    }

    @Test
    void findByIdUserNotFoundExceptionTest() {
        final Long id = 1L;

        when(userRepository.findById(id))
                .thenReturn(Optional.empty());

        final var exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.findById(id)
        );

        assertThat("User с идентификатором " + id + " не найден.", equalTo(exception.getMessage()));

        verify(userRepository, times(1))
                .findById(id);
    }

    @Test
    void findAll() {
        List<User> users = List.of(user);

        when(userRepository.findAll())
                .thenReturn(users);

        final var result = userService.findAll();

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(userDto.getId()));
        assertThat(result.get(0).getName(), equalTo(userDto.getName()));
        assertThat(result.get(0).getEmail(), equalTo(userDto.getEmail()));

        verify(userRepository, times(1))
                .findAll();
    }

    @Test
    void deleteByIdTest() {
        final Long id = 1L;

        doNothing()
                .when(userRepository).deleteById(id);

        userService.deleteById(id);

        verify(userRepository, times(1))
                .deleteById(id);
    }
}