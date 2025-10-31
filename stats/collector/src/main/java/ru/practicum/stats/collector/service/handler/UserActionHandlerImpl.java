package ru.practicum.stats.collector.service.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.ewm.stats.proto.UserActionProto;
import ru.practicum.stats.collector.service.UserActionProducer;

import java.time.Instant;

@RequiredArgsConstructor
@Service
public class UserActionHandlerImpl implements UserActionHandler {

    private final UserActionProducer producer;

    @Override
    public void handle(UserActionProto userActionProto) {
        producer.sendUserAction(mapToAvro(userActionProto));
    }

    private UserActionAvro mapToAvro(UserActionProto userActionProto) {
        Instant timestamp = Instant.ofEpochSecond(
                userActionProto.getTimestamp().getSeconds(),
                userActionProto.getTimestamp().getNanos()
        );

        return UserActionAvro.newBuilder()
                .setUserId(userActionProto.getUserId())
                .setEventId(userActionProto.getEventId())
                .setActionType(ActionTypeAvro.valueOf(userActionProto.getActionType().name()))
                .setTimestamp(timestamp)
                .build();
    }
}
