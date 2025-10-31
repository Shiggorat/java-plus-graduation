package ru.practicum.rating.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@Getter
@Setter
public class UpdateRatingParam {
    private Long ratingId;
    private UpdateRatingDto updateRatingDto;
}
