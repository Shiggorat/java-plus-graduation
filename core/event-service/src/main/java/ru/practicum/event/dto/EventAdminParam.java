package ru.practicum.event.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;
import ru.practicum.enums.event.EventState;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class EventAdminParam {
    private List<Long> users;
    private List<EventState> states;
    private List<Long> categories;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private Pageable pageable;
}
