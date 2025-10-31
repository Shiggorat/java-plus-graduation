package ru.practicum.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.contract.RequestOperations;
import ru.practicum.request.service.RequestInternalService;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/requests")
@Slf4j
public class InternalRequestController implements RequestOperations {

    private final RequestInternalService requestService;

    @Override
    @PostMapping("/count")
    public Map<Long, Integer> getCountConfirmedRequestsByEventIds(@RequestBody List<Long> eventsIds) {
        log.info("Посчитать запросы на участие в мероприятиях: {}", eventsIds);
        Map<Long, Integer> requestsCountByEvent = requestService.getCountConfirmedRequestsByEventIds(eventsIds);
        log.info("Посчитанные запросы: {}", requestsCountByEvent);
        return requestsCountByEvent;
    }

    @Override
    @GetMapping("/count/{eventId}")
    public int getCountConfirmedRequestsByEventId(@PathVariable Long eventId) {
        log.info("Посчитать запросы на участие в мероприятии: {}", eventId);
        int requestCount = requestService.getCountConfirmedRequestsByEventId(eventId);
        log.info("Посчитанные запросы для мероприятия {}: {}", eventId, requestCount);
        return requestCount;
    }
}
