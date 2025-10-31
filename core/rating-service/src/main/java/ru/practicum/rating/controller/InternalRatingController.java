package ru.practicum.rating.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.contract.RatingOperations;
import ru.practicum.dto.rating.EventSearchByRatingParam;
import ru.practicum.rating.service.RatingInternalService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/rating")
public class InternalRatingController implements RatingOperations {

    private final RatingInternalService ratingInternalService;

    @Override
    @GetMapping
    public List<Long> getMostLikedEventIds(@RequestParam EventSearchByRatingParam param) {
        return ratingInternalService.getMostLikedEventIds(param);
    }
}
