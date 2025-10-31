package ru.practicum.event.service;

import ru.practicum.dto.rating.EventSearchByRatingParam;
import ru.practicum.event.dto.EventShortDto;

import java.util.List;

public interface EventSearchService {
    List<EventShortDto> searchMostLikedEvents(EventSearchByRatingParam eventSearchByRatingParam);
}
