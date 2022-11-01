package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    @Override
    <S extends ItemRequest> S save(S id);

    @Override
    boolean existsById(Long id);

    List<ItemRequest> findByRequesterIdIsOrderByCreatedDesc(Long requesterId);

    List<ItemRequest> findAllByRequesterIdNotOrderByCreatedDesc(Long userId, Pageable pageable);

    @Override
    Optional<ItemRequest> findById(Long requestId);

    @Override
    Page<ItemRequest> findAll(Pageable pageable);

}