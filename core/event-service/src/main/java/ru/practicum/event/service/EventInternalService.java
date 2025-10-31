package ru.practicum.event.service;

import ru.practicum.dto.event.EventForRequestDto;

public interface EventInternalService {
    EventForRequestDto getById(Long id);

    boolean checkUserIsInitiator(long userId, long eventId);

    void checkExistsById(long id);

}