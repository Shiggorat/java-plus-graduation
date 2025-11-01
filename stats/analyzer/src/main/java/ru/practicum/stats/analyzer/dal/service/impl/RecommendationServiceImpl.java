package ru.practicum.stats.analyzer.dal.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.proto.InteractionsCountRequestProto;
import ru.practicum.ewm.stats.proto.RecommendedEventProto;
import ru.practicum.ewm.stats.proto.SimilarEventsRequestProto;
import ru.practicum.ewm.stats.proto.UserPredictionsRequestProto;
import ru.practicum.stats.analyzer.dal.model.EventSimilarity;
import ru.practicum.stats.analyzer.dal.model.UserAction;
import ru.practicum.stats.analyzer.dal.repository.EventSimilarityRepository;
import ru.practicum.stats.analyzer.dal.repository.UserActionRepository;
import ru.practicum.stats.analyzer.dal.service.RecommendationService;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final UserActionRepository userActionRepository;
    private final EventSimilarityRepository eventSimilarityRepository;

    @Override
    public Stream<RecommendedEventProto> getRecommendationsForUser(UserPredictionsRequestProto request) {
        long userId = request.getUserId();
        List<Long> userEventIds = userActionRepository.findByUserId(userId, PageRequest.of(0, request.getMaxResults(),
                Sort.by(Sort.Direction.DESC, "timestamp")));
        if (userEventIds.isEmpty()) {
            return Stream.empty();
        }

        PageRequest pageRequest = PageRequest.of(0, request.getMaxResults(),
                Sort.by(Sort.Direction.DESC, "score"));
        List<EventSimilarity> similarities = eventSimilarityRepository.findNewSimilar(userEventIds, pageRequest);

        Map<Long, Double> predictedScores = predictScores(similarities, userEventIds);

        return mapToProto(predictedScores);
    }

    @Override
    public Stream<RecommendedEventProto> getSimilarEvents(SimilarEventsRequestProto request) {
        long eventId = request.getEventId();
        List<EventSimilarity> similarities = eventSimilarityRepository.findAllByEventId(eventId);
        Set<Long> userEventIds = userActionRepository
                .findByUserIdExcludeEventId(request.getUserId(), eventId);

        similarities = similarities.stream()
                .filter(s -> !(userEventIds.contains(s.getEventA()) || userEventIds.contains(s.getEventB())))
                .collect(Collectors.toList());

        similarities.sort(Comparator.comparingDouble(EventSimilarity::getScore).reversed());
        int n = Math.min(request.getMaxResults(), similarities.size());
        similarities = similarities.subList(0, n);

        return mapToProto(similarities, eventId);
    }

    @Override
    public Stream<RecommendedEventProto> getInteractionsCount(InteractionsCountRequestProto request) {
        Map<Long, Double> sumOfWeightsByEvent = new HashMap<>();
        List<UserAction> userActions = userActionRepository.findByEventIdIn(new HashSet<>(request.getEventIdList()));
        for (UserAction userAction : userActions) {
            long eventId = userAction.getEventId();
            double sum = sumOfWeightsByEvent.getOrDefault(eventId, 0.0);
            double weight = userAction.getWeight();
            sum += weight;
            sumOfWeightsByEvent.put(eventId, sum);
        }
        return mapToProto(sumOfWeightsByEvent);
    }


    private Map<Long, Double> predictScores(List<EventSimilarity> similarities, List<Long> userEvents) {
        Map<Long, Double> scoreByEvent = new HashMap<>();

        Set<Long> allEventIds = new HashSet<>();
        for (EventSimilarity s : similarities) {
            allEventIds.add(s.getEventA());
            allEventIds.add(s.getEventB());
        }
        allEventIds.addAll(userEvents);

        Map<Long, List<EventSimilarity>> neighboursMap = new HashMap<>();
        for (Long eventId : allEventIds) {
            List<EventSimilarity> neighbours = eventSimilarityRepository.findNeighbours(eventId, PageRequest.of(0,
                    5, Sort.by(Sort.Direction.DESC, "score")));
            neighboursMap.put(eventId, neighbours);
        }

        Set<Long> neighboursIds = new HashSet<>();
        for (Map.Entry<Long, List<EventSimilarity>> entry : neighboursMap.entrySet()) {
            for (EventSimilarity n : entry.getValue()) {
                long eventA = n.getEventA();
                long eventB = n.getEventB();
                Long neighbourId = (eventA == entry.getKey()) ? eventB : eventA;
                neighboursIds.add(neighbourId);
            }
        }

        List<UserAction> allUserActions = userActionRepository.findByEventIdIn(neighboursIds);
        Map<Long, List<UserAction>> userActionsMap = new HashMap<>();
        for (UserAction ua : allUserActions) {
            long eventId = ua.getEventId();
            userActionsMap.computeIfAbsent(eventId, k -> new ArrayList<>()).add(ua);
        }

        for (EventSimilarity s : similarities) {
            long candidate = userEvents.contains(s.getEventA()) ? s.getEventB() : s.getEventA();

            List<EventSimilarity> neighbours = neighboursMap.get(candidate);

            List<Long> neighboursIdsList = neighbours.stream()
                    .map(n -> {
                        long eventA = n.getEventA();
                        long eventB = n.getEventB();
                        return (eventA == candidate) ? eventB : eventA;
                    })
                    .toList();

            List<UserAction> userActions = new ArrayList<>();
            for (Long neighbourId : neighboursIdsList) {
                List<UserAction> actions = userActionsMap.getOrDefault(neighbourId, Collections.emptyList());
                userActions.addAll(actions);
            }

            double predictedScore = calculatePredictedScore(neighbours, userActions, candidate);
            scoreByEvent.put(candidate, predictedScore);
        }

        return scoreByEvent;
    }

    private double calculatePredictedScore(List<EventSimilarity> neighbours, List<UserAction> userActions, long candidateId) {
        Map<Long, Double> weightByEvent = userActions.stream()
                .collect(Collectors.toMap(UserAction::getEventId, UserAction::getWeight));

        double sumOfWeightedScore = 0.0;
        double sumOfSimilarity = 0.0;
        for (EventSimilarity neighbour : neighbours) {
            long neighbourId = (neighbour.getEventA() == candidateId)
                    ? neighbour.getEventB()
                    : neighbour.getEventA();
            double weight = weightByEvent.getOrDefault(neighbourId, 0.0);
            double similarity = neighbour.getScore();
            double weightedScore = weight * similarity;
            sumOfWeightedScore += weightedScore;
            sumOfSimilarity += similarity;
        }

        return sumOfSimilarity == 0 ? 0.0 : sumOfWeightedScore / sumOfSimilarity;
    }

    private Stream<RecommendedEventProto> mapToProto(List<EventSimilarity> eventSimilarities, long currentEventId) {
        return eventSimilarities.stream()
                .map(s -> {
                    long recommended = (s.getEventA() == currentEventId) ? s.getEventB() : s.getEventA();
                    return RecommendedEventProto.newBuilder()
                            .setEventId(recommended)
                            .setScore(s.getScore())
                            .build();
                });
    }

    private Stream<RecommendedEventProto> mapToProto(Map<Long, Double> scoreByEvent) {
        return scoreByEvent.entrySet().stream()
                .map(p -> RecommendedEventProto.newBuilder()
                        .setEventId(p.getKey())
                        .setScore(p.getValue())
                        .build());
    }
}
