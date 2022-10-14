package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Override
    <S extends Item> S save(S item);

    @Override
    boolean existsById(Long id);

    @Override
    void deleteById(Long id);

    @Override
    List<Item> findAll();

    @Query("select i " +
            "from Item i " +
            "where i.available = true " +
            "and (lower(i.name) like concat('%', lower(?1), '%') " +
            "or lower(i.description) like concat('%', lower(?1), '%')) ")
    List<Item> itemSearch(String text);

    List<Item> findAllByOwnerIdOrderByIdAsc(Long id);

    @Override
    Optional<Item> findById(Long id);

}
