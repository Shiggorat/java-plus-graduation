package ru.practicum.rating.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.rating.mark.Mark;

@Entity
@Table(name = "ratings", schema = "core_rating")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private long userId;
    @Column(name = "event_id")
    private long eventId;
    @Column(name = "mark")
    @Enumerated(value = EnumType.STRING)
    private Mark mark;
}