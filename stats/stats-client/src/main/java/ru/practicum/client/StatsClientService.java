package ru.practicum.client;

import ru.practicum.dto.ResponseStatDto;
import ru.practicum.dto.StatDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsClientService {
    List<ResponseStatDto> getStats(LocalDateTime start,
                                  LocalDateTime end,
                                  List<String> uris,
                                  boolean unique);

    StatDto hit(StatDto statDto);
}
