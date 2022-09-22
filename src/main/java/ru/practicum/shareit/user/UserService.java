package ru.practicum.shareit.user;


import java.util.Collection;

public interface UserService {

    UserDto create(UserDto userDto);

    UserDto findById(Long id);

    Collection<UserDto> findAll();

    void deleteById(Long id);

    UserDto update(Long id, UserDto userDto);
}
