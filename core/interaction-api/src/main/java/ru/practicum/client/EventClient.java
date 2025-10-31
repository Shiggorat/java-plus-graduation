package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.client.config.FeignClientConfig;
import ru.practicum.contract.EventOperations;

@FeignClient(name = "event-service", path = "/internal/events", configuration = FeignClientConfig.class)
public interface EventClient extends EventOperations {

}