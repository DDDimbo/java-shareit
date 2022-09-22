package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.AlreadyExistEmailException;
import ru.practicum.shareit.exceptions.NonExistedUserIdException;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto userDto) throws NonExistedUserIdException {
        if (userRepository.containsEmail(userDto.getEmail()))
            throw new AlreadyExistEmailException("Данный email уже занят");
        User resUser = UserMapper.toUser(userDto);
        Long userId = userRepository.save(resUser);
        return UserMapper.toUserDto(userRepository.getUserById(userId));
    }

    @Override
    public UserDto update(Long id, UserDto userDto) throws NonExistedUserIdException {
        User user = userRepository.getUserById(id);
        if (userDto.getEmail() != null) {
            if (userRepository.containsEmail(userDto.getEmail()))
                throw new AlreadyExistEmailException("Данный email уже занят");
            else if (userRepository.containsEmail(userDto.getEmail()) && userDto.getEmail().equals(user.getEmail()))
                throw new AlreadyExistEmailException("Новый email совпадает со старым");
            userRepository.deleteEmail(user.getEmail());
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null)
            user.setName(userDto.getName());

        userRepository.put(id, user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto findById(Long id) throws NonExistedUserIdException {
        User user = userRepository.getUserById(id);
        return UserMapper.toUserDto(user);
    }

    @Override
    public Collection<UserDto> findAll() throws NonExistedUserIdException {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) throws NonExistedUserIdException {
        userRepository.deleteById(id);
    }

}
