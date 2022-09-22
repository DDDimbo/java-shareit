package ru.practicum.shareit.user;

import java.util.List;

public interface UserRepository {

    Long save(User user);

    void deleteById(Long id);

    void deleteEmail(String email);

    List<User> findAll();

    boolean containsEmail(String email);

    User getUserById(Long id);

    void put(Long id, User user);

    boolean containsUser(Long id);

}
