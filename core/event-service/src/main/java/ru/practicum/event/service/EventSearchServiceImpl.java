package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.RatingClient;
import ru.practicum.dto.rating.EventSearchByRatingParam;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventSearchServiceImpl implements EventSearchService {
    private final RatingClient ratingClient;
    private final EventService eventService;
    private final EventRepository eventRepository;

    @Transactional
    @Override
    public List<EventShortDto> searchMostLikedEvents(EventSearchByRatingParam eventSearchByRatingParam) {
        List<Long> eventsIds = ratingClient.getMostLikedEventIds(eventSearchByRatingParam);
        List<Event> events = eventRepository.findAllByIdIn(eventsIds);

        Map<Long, Event> eventMap = events.stream()
                .collect(Collectors.toMap(Event::getId, Function.identity()));

        List<Event> orderedEvents = eventsIds.stream()
                .map(eventMap::get)
                .filter(Objects::nonNull)
                .toList();

        return eventService.getShortEvents(orderedEvents);
    }
}
