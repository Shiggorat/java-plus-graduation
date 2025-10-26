package ru.practicum.request.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.enums.request.RequestStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class ParticipationRequestDto {
    private Long id;
    private Long requester;
    private Long event;
    private RequestStatus status;
    private LocalDateTime created;
}