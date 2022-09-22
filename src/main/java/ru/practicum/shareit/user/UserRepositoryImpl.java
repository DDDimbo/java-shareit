package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NonExistedUserIdException;

import java.util.*;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private static final Set<String> emails = new HashSet<>();

    private static final Map<Long, User> users = new HashMap<>();

    private static long id;


    // Также возвращает id созданного пользователя
    @Override
    public Long save(User user) {
        emails.add(user.getEmail());
        id++;
        user.setId(id);
        users.put(id, user);
        return id;

    }

    @Override
    public void put(Long userId, User user) {
        emails.add(user.getEmail());
        users.put(userId, user);
    }

    @Override
    public void deleteById(Long id) {
        idCheck(id);

        // Удаление из хранилища с пользователем и из хранилища c email
        emails.remove(users.get(id).getEmail());
        users.remove(id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public boolean containsEmail(String email) {
        return emails.contains(email);
    }

    @Override
    public boolean containsUser(Long id) {
        return users.containsKey(id);
    }

    @Override
    public void deleteEmail(String email) {
        emails.remove(email);
    }

    @Override
    public User getUserById(Long id) {
        idCheck(id);
        return users.get(id);
    }

    private static void idCheck(Long id) {
        if (!users.containsKey(id))
            throw new NonExistedUserIdException("Пользователь с таким id не существует");
    }

}
