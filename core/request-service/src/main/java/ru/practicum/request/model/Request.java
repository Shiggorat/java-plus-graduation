package ru.practicum.request.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.enums.request.RequestStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "requests", schema = "core_requests")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    @Column(name = "requester_id")
    private long requesterId;

    @Column(name = "event_id")
    private long eventId;
}
