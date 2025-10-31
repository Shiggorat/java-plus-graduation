package ru.practicum.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.enums.event.EventState;

@Data
@Builder
@AllArgsConstructor
public class EventForRequestDto {
    private Long id;
    private Long initiatorId;
    private Integer participantLimit;
    private EventState state;
    private Boolean requestModeration;
}
