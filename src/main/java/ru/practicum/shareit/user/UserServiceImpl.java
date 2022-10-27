package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.AlreadyExistsEmailException;
import ru.practicum.shareit.exceptions.UserNotFoundException;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;


    @Transactional
    @Override
    public UserDto create(UserDto userDto) throws UserNotFoundException {
        User resUser = UserMapper.toUser(userDto);
        Long userId = userRepository.save(resUser).getId();
        return UserMapper.toUserDto(userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User с идентификатором " + userId + " не найден.")));
    }


    @Transactional
    @Override
    public UserDto update(Long id, UserDto userDto) throws UserNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User с идентификатором " + id + " не найден."));
        if (userDto.getEmail() != null) {
            if (userRepository.existsByEmail(userDto.getEmail()) && userDto.getEmail().equals(user.getEmail()))
                throw new AlreadyExistsEmailException("Новый email совпадает со старым");
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null)
            user.setName(userDto.getName());

        userRepository.save(user);
        return UserMapper.toUserDto(user);
    }


    @Override
    public UserDto findById(Long id) throws UserNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User с идентификатором " + id + " не найден."));
        return UserMapper.toUserDto(user);
    }


    @Override
    public Collection<UserDto> findAll() throws UserNotFoundException {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void deleteById(Long id) throws UserNotFoundException {
        userRepository.deleteById(id);
    }

}
