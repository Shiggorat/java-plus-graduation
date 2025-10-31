package ru.practicum.dto.rating;

import lombok.*;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class EventSearchByRatingParam {
    private int limit;
}
