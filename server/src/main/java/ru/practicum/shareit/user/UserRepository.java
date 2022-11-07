package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Override
    <S extends User> S save(S user);


    @Override
    Optional<User> findById(Long id);

    @Override
    List<User> findAll();

    @Override
    boolean existsById(Long id);

    boolean existsByEmail(String email);

    @Override
    void deleteById(Long id);
}
