package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.client.config.FeignClientConfig;
import ru.practicum.contract.UserOperations;

@FeignClient(name = "user-service", path = "/internal/users", configuration = FeignClientConfig.class)
public interface UserClient extends UserOperations {

}
