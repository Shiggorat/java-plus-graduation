package ru.practicum.rating.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.rating.mark.Mark;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingDto {
    private Long id;
    private Long userId;
    private Long eventId;
    private Mark mark;
}
