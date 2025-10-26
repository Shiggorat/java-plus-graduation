package ru.practicum.client;


import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.client.config.FeignClientConfig;
import ru.practicum.contract.RatingOperations;

@FeignClient(name = "rating-service", path = "/internal/rating", configuration = FeignClientConfig.class)
public interface RatingClient extends RatingOperations {
}
