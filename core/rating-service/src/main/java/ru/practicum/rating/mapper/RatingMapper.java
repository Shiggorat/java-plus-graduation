package ru.practicum.rating.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.practicum.rating.dto.RatingDto;
import ru.practicum.rating.model.Rating;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RatingMapper {
    RatingDto toRatingDto(Rating rating);
}
