package ru.practicum.rating.service;

import ru.practicum.dto.rating.EventSearchByRatingParam;

import java.util.List;

public interface RatingInternalService {
    List<Long> getMostLikedEventIds(EventSearchByRatingParam param);
}
