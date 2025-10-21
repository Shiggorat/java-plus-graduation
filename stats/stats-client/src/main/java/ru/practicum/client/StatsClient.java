package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.contract.StatsOperations;

@FeignClient(name = "stats-server")
public interface StatsClient extends StatsOperations {
}