package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.event.EventForRequestDto;
import ru.practicum.errors.exceptions.NotFoundException;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;

@RequiredArgsConstructor
@Service
public class EventInternalServiceImpl implements EventInternalService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    @Override
    public EventForRequestDto getById(Long id) {
        Event event = eventRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Событие с id %d не найдено", id)));
        return eventMapper.toEventForRequestDto(event);
    }

    @Override
    public boolean checkUserIsInitiator(long userId, long eventId) {
        return eventRepository.existsByInitiatorIdAndId(userId, eventId);
    }

    @Override
    public void checkExistsById(long id) {
        boolean exists = eventRepository.existsById(id);
        if (!exists) {
            throw new NotFoundException(String.format("Событие с id %d не найдено", id));
        }
    }
}
