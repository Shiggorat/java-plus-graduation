package ru.practicum.stats.analyzer.handler.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.stats.analyzer.dal.model.UserAction;
import ru.practicum.stats.analyzer.dal.service.UserActionService;
import ru.practicum.stats.analyzer.handler.UserActionHandler;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class UserActionHandlerImpl implements UserActionHandler {
    private final static double VIEW_WEIGHT = 0.4;
    private final static double REGISTER_WEIGHT = 0.8;
    private final static double LIKE_WEIGHT = 1;

    private static final Map<ActionTypeAvro, Double> WEIGHTS = Map.of(
            ActionTypeAvro.ACTION_VIEW, VIEW_WEIGHT,
            ActionTypeAvro.ACTION_REGISTER, REGISTER_WEIGHT,
            ActionTypeAvro.ACTION_LIKE, LIKE_WEIGHT
    );

    private final UserActionService userActionService;

    @Override
    public void handle(UserActionAvro userActionAvro) {
        UserAction userAction = map(userActionAvro);
        userActionService.save(userAction);
    }

    UserAction map(UserActionAvro userActionAvro) {
        return UserAction.builder()
                .userId(userActionAvro.getUserId())
                .eventId(userActionAvro.getEventId())
                .weight(WEIGHTS.get(userActionAvro.getActionType()))
                .timestamp(userActionAvro.getTimestamp())
                .build();
    }
}
