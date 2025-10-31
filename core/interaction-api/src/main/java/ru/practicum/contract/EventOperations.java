package ru.practicum.contract;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.dto.event.EventForRequestDto;

public interface EventOperations {

    @GetMapping("/{id}")
    EventForRequestDto getById(@PathVariable Long id);

    @GetMapping("check/user/{userId}/event/{eventId}")
    boolean checkUserIsInitiator(@PathVariable long userId, @PathVariable long eventId);

    @GetMapping("/exists/{id}")
    void checkExistsById(@PathVariable long id);
}
