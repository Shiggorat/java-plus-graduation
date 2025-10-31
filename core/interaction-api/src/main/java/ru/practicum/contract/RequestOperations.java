package ru.practicum.contract;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

public interface RequestOperations {

    @PostMapping("/count")
    Map<Long, Integer> getCountConfirmedRequestsByEventIds(@RequestBody List<Long> eventsIds);

    @GetMapping("/count/{eventId}")
    int getCountConfirmedRequestsByEventId(@PathVariable Long eventId);
}
