package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    @Override
    <S extends ItemRequest> S save(S id);

    @Override
    boolean existsById(Long id);

    List<ItemRequest> findAllByRequesterIdIsOrderByCreatedDesc(Long requesterId);

    @Override
    Optional<ItemRequest> findById(Long requestId);

    @Override
    Page<ItemRequest> findAll(Pageable pageable);

    List<ItemRequest> findAllByRequesterIdNotOrderByCreatedDesc(Long userId, Pageable pageable);
}
