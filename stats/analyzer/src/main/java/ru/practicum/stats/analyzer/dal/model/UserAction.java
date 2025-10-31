package ru.practicum.stats.analyzer.dal.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Getter
@Setter
@ToString
@Builder
@Table(name = "user_action", schema = "stats_analyzer")
@NoArgsConstructor
@AllArgsConstructor
public class UserAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "user_id")
    private long userId;
    @Column(name = "event_id")
    private long eventId;
    private double weight;
    private Instant timestamp;
}
