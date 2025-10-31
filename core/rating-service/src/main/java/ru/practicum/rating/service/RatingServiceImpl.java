package ru.practicum.rating.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.EventClient;
import ru.practicum.client.UserClient;
import ru.practicum.errors.exceptions.ConditionsNotMetException;
import ru.practicum.errors.exceptions.NotFoundException;
import ru.practicum.rating.dto.NewRatingDto;
import ru.practicum.rating.dto.RatingDto;
import ru.practicum.rating.dto.UpdateRatingDto;
import ru.practicum.rating.mapper.RatingMapper;
import ru.practicum.rating.mark.Mark;
import ru.practicum.rating.model.Rating;
import ru.practicum.rating.repository.RatingRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {
    private final RatingRepository ratingRepository;
    private final RatingMapper ratingMapper;
    private final UserClient userClient;
    private final EventClient eventClient;

    private static Rating getRating(NewRatingDto newRatingDto, long userId, long eventId) {
        return Rating.builder()
                .userId(userId)
                .eventId(eventId)
                .mark(newRatingDto.getMark())
                .build();
    }

    @Override
    public RatingDto create(long userId, long eventId, NewRatingDto newRatingDto) {
        boolean exists = ratingRepository.existsByUserIdAndEventId(userId, eventId);
        if (exists) {
            throw new ConditionsNotMetException(
                    String.format("Пользователь с ID %d уже оценивал событие с ID %d", userId, eventId));
        }

        eventClient.checkExistsById(eventId);
        userClient.checkUserExists(userId);
        return ratingMapper.toRatingDto(saveRating(newRatingDto, userId, eventId));
    }

    @Override
    @Transactional
    public RatingDto update(long userId, long eventId, long ratingId, UpdateRatingDto updateRatingDto) {
        Rating rating = ratingRepository.findByIdAndUserId(ratingId, userId).orElseThrow(() ->
                new NotFoundException(String.format(
                        "Rating with userId = %d and id = %d was not found", userId, ratingId)));
        Mark newMark = updateRatingDto.getMark();
        rating.setMark(newMark);

        Mark oldMark = rating.getMark();
        if (newMark != oldMark) {
            log.info("Rating updated: {}", rating);
        }

        return ratingMapper.toRatingDto(rating);
    }

    @Override
    @Transactional
    public void delete(long userId, long eventId, long ratingId) {
        Rating ratingMark = ratingRepository.findById(ratingId).orElseThrow(() ->
                new NotFoundException(String.format(
                        "Rating mark not found. id = '%d'", ratingId))
        );

        if (userId != ratingMark.getUserId()) {
            throw new ConditionsNotMetException("User with id = " + userId + " is not author of mark");
        }

        eventClient.checkExistsById(eventId);

        ratingRepository.deleteById(ratingId);
        log.info("Rating mark '{}' deleted", ratingId);
    }

    @Transactional
    private Rating saveRating(NewRatingDto dto, long userId, long eventId) {
        Rating newMark = getRating(dto, userId, eventId);
        return ratingRepository.save(newMark);
    }
}