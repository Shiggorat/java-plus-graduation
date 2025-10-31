package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.client.config.FeignClientConfig;
import ru.practicum.contract.RequestOperations;

@FeignClient(name = "request-service", path = "/internal/requests", configuration = FeignClientConfig.class)
public interface RequestClient extends RequestOperations {
}