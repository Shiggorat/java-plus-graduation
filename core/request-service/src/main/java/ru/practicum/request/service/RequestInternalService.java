package ru.practicum.request.service;

import java.util.List;
import java.util.Map;

public interface RequestInternalService {

    Map<Long, Integer> getCountConfirmedRequestsByEventIds(List<Long> eventsIds);

    int getCountConfirmedRequestsByEventId(Long eventId);
}
