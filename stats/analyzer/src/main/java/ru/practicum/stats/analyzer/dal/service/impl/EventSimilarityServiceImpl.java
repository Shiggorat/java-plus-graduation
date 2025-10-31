package ru.practicum.stats.analyzer.dal.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.stats.analyzer.dal.model.EventSimilarity;
import ru.practicum.stats.analyzer.dal.repository.EventSimilarityRepository;
import ru.practicum.stats.analyzer.dal.service.EventSimilarityService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventSimilarityServiceImpl implements EventSimilarityService {
    private final EventSimilarityRepository eventSimilarityRepository;

    @Override
    @Transactional
    public void save(EventSimilarity eventSimilarity) {
        Optional<EventSimilarity> maybeSimilarity = eventSimilarityRepository
                .findByEventAAndEventB(eventSimilarity.getEventA(), eventSimilarity.getEventB());
        if (maybeSimilarity.isPresent()) {
            EventSimilarity oldSimilarity = maybeSimilarity.get();
            if (oldSimilarity.getScore() != eventSimilarity.getScore()) {
                oldSimilarity.setScore(eventSimilarity.getScore());
            }
            eventSimilarityRepository.save(oldSimilarity);
        } else {
            eventSimilarityRepository.save(eventSimilarity);
        }
    }
}
