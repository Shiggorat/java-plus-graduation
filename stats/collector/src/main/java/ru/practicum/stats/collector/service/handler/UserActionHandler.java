package ru.practicum.stats.collector.service.handler;

import ru.practicum.ewm.stats.proto.UserActionProto;

public interface UserActionHandler {
    void handle(UserActionProto userActionProto);
}
