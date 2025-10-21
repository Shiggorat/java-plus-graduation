package ru.practicum.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.dto.ResponseStatDto;
import ru.practicum.dto.StatDto;

import java.time.LocalDateTime;
import java.util.List;
@Service
@RequiredArgsConstructor
public class StatsClientServiceImpl implements StatsClientService {

    private final StatsClient statsClient;

    @Override
    public List<ResponseStatDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        return statsClient.getStats(start, end, uris, unique);
    }

    @Override
    public StatDto hit(StatDto statDto) {
        return statsClient.saveHitRequest(statDto);
    }
}
