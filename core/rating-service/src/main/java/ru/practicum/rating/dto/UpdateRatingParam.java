package ru.practicum.rating.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UpdateRatingParam {
    private Long ratingId;
    private UpdateRatingDto updateRatingDto;
}
