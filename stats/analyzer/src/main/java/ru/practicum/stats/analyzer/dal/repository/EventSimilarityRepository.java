package ru.practicum.stats.analyzer.dal.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.stats.analyzer.dal.model.EventSimilarity;

import java.util.List;
import java.util.Optional;

public interface EventSimilarityRepository extends JpaRepository<EventSimilarity, Long> {
    Optional<EventSimilarity> findByEventAAndEventB(long eventA, long eventB);

    @Query("select s from EventSimilarity s where s.eventA = :eventId or s.eventB = :eventId")
    List<EventSimilarity> findAllByEventId(long eventId);

    @Query("select s from EventSimilarity s " +
            "where (s.eventA IN :eventIds OR s.eventB IN :eventIds) " +
            "and not (s.eventA IN :eventIds AND s.eventB IN :eventIds)")
    List<EventSimilarity> findNewSimilar(List<Long> eventIds, Pageable pageable);

    @Query("select s from EventSimilarity s where s.eventA = :eventId or s.eventB = :eventId")
    List<EventSimilarity> findNeighbours(long eventId, Pageable pageable);
}
