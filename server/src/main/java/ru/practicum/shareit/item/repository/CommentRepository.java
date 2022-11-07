package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.dto.CommentPrintView;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Override
    <S extends Comment> S save(S comment);

    @Override
    Optional<Comment> findById(Long id);

    @Override
    void deleteById(Long id);

    @Override
    boolean existsById(Long id);

    List<CommentPrintView> findFirst10ByItemIdOrderByCreatedDesc(Long itemId);

}
