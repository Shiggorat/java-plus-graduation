package ru.practicum.rating.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.rating.model.Rating;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    boolean existsByUserIdAndEventId(long userId, long eventId);

    Optional<Rating> findByIdAndUserId(long ratingId, long userId);

    @Query("SELECT r.eventId " +
            "FROM Rating r " +
            "WHERE r.mark = 'LIKE' " +
            "GROUP BY r.eventId " +
            "ORDER BY COUNT(r) DESC")
    List<Long> findMostLikedEvents(Pageable pageable);
}
