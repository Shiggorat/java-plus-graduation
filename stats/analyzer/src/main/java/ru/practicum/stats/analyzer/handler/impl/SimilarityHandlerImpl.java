package ru.practicum.stats.analyzer.handler.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.stats.analyzer.dal.model.EventSimilarity;
import ru.practicum.stats.analyzer.dal.service.EventSimilarityService;
import ru.practicum.stats.analyzer.handler.SimilarityHandler;

@Component
@RequiredArgsConstructor
public class SimilarityHandlerImpl implements SimilarityHandler {
    private final EventSimilarityService eventSimilarityService;

    @Override
    public void handle(EventSimilarityAvro eventSimilarityAvro) {
        EventSimilarity eventSimilarity = map(eventSimilarityAvro);
        eventSimilarityService.save(eventSimilarity);
    }

    EventSimilarity map(EventSimilarityAvro eventSimilarityAvro) {
        return EventSimilarity.builder()
                .eventA(eventSimilarityAvro.getEventA())
                .eventB(eventSimilarityAvro.getEventB())
                .score(eventSimilarityAvro.getScore())
                .build();
    }
}
