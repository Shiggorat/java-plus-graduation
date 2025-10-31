package ru.practicum.stats.analyzer.dal.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.stats.analyzer.dal.model.UserAction;
import ru.practicum.stats.analyzer.dal.repository.UserActionRepository;
import ru.practicum.stats.analyzer.dal.service.UserActionService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserActionServiceImpl implements UserActionService {

    private final UserActionRepository userActionRepository;

    @Override
    public void save(UserAction userAction) {
        Optional<UserAction> maybeUserAction = userActionRepository.findByUserIdAndEventId(userAction.getUserId(), userAction.getEventId());
        if (maybeUserAction.isPresent()) {
            UserAction oldUserAction = maybeUserAction.get();
            if (userAction.getWeight() > oldUserAction.getWeight()) {
                oldUserAction.setWeight(userAction.getWeight());
                userActionRepository.save(oldUserAction);
            }
        } else {
            userActionRepository.save(userAction);
        }
    }
}
