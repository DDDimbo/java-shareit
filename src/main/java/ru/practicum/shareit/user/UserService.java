package ru.practicum.shareit.user;


import java.util.List;

public interface UserService {

    UserDto create(UserDto userDto);

    UserDto findById(Long id);

    List<UserDto> findAll();

    void deleteById(Long id);

    UserDto update(Long id, UserDto userDto);
}
