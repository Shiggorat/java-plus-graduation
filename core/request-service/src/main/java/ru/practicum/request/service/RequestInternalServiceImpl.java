package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.request.repository.RequestRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RequestInternalServiceImpl implements RequestInternalService {

    private final RequestRepository requestRepository;

    @Override
    public Map<Long, Integer> getCountConfirmedRequestsByEventIds(List<Long> eventsIds) {
        return requestRepository.findCountConfirmedByEventIds(eventsIds).stream()
                .collect(Collectors.toMap(
                        RequestRepository.EventRequestCount::getEventId,
                        RequestRepository.EventRequestCount::getCount
                ));
    }

    @Override
    public int getCountConfirmedRequestsByEventId(Long eventId) {
        return requestRepository.findCountOfConfirmedRequestsByEventId(eventId);
    }
}
