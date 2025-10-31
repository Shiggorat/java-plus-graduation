package ru.practicum.stats.analyzer.handler;

import ru.practicum.ewm.stats.avro.UserActionAvro;

public interface UserActionHandler {

    void handle(UserActionAvro userActionAvro);
}
