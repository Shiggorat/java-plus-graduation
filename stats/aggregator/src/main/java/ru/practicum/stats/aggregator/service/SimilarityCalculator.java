package ru.practicum.stats.aggregator.service;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SimilarityCalculator {

    private final static double VIEW_WEIGHT = 0.4;
    private final static double REGISTER_WEIGHT = 0.8;
    private final static double LIKE_WEIGHT = 1.0;

    private static final Map<ActionTypeAvro, Double> WEIGHTS = Map.of(
            ActionTypeAvro.ACTION_VIEW, VIEW_WEIGHT,
            ActionTypeAvro.ACTION_REGISTER, REGISTER_WEIGHT,
            ActionTypeAvro.ACTION_LIKE, LIKE_WEIGHT
    );

    // eventId -> (userId -> weight)
    private final Map<Long, Map<Long, Double>> weightMatrix = new ConcurrentHashMap<>();

    // eventId -> total weight sum
    private final Map<Long, Double> weightSumByEvent = new HashMap<>();

    // eventIdA -> (eventIdB -> minSum)
    private final Map<Long, Map<Long, Double>> minWeightsSums = new HashMap<>();

    // userId -> set of events interacted
    private final Map<Long, Set<Long>> eventsByUser = new HashMap<>();

    public List<EventSimilarityAvro> calculateSimilarity(UserActionAvro userActionAvro) {
        long eventId = userActionAvro.getEventId();
        long userId = userActionAvro.getUserId();

        double actionWeight = WEIGHTS.get(userActionAvro.getActionType());

        Map<Long, Double> userWeights = weightMatrix.computeIfAbsent(eventId, k -> new HashMap<>());
        double oldWeight = userWeights.getOrDefault(userId, 0.0);

        if (actionWeight > oldWeight) {
            userWeights.put(userId, actionWeight);
            weightMatrix.put(eventId, userWeights);

            double totalWeight = weightSumByEvent.getOrDefault(eventId, 0.0);
            totalWeight = totalWeight - oldWeight + actionWeight;
            weightSumByEvent.put(eventId, totalWeight);

            Set<Long> userEvents = eventsByUser.computeIfAbsent(userId, k -> new HashSet<>());

            List<EventSimilarityAvro> similarities = new ArrayList<>();

            for (Long otherEventId : userEvents) {
                if (otherEventId == eventId) continue;

                double similarity = recalculatePair(eventId, otherEventId, userId, oldWeight, actionWeight);
                similarities.add(mapToEventSimilarityAvro(eventId, otherEventId, similarity));
            }

            userEvents.add(eventId);

            return similarities;
        }

        return Collections.emptyList();
    }

    private double recalculatePair(long eventA, long eventB, long userId, double oldWeight, double newWeight) {

        double similarEventWeight = weightMatrix.getOrDefault(eventB, Map.of()).getOrDefault(userId, 0.0);

        double oldContribution = Math.min(oldWeight, similarEventWeight);
        double newContribution = Math.min(newWeight, similarEventWeight);
        double diff = newContribution - oldContribution;

        double minSum = getMinSum(eventA, eventB) + diff;
        putMinSum(eventA, eventB, minSum);

        double totalA = weightSumByEvent.getOrDefault(eventA, 0.0);
        double totalB = weightSumByEvent.getOrDefault(eventB, 0.0);

        return minSum / (Math.sqrt(totalA) * Math.sqrt(totalB));
    }

    private void putMinSum(long eventA, long eventB, double sum) {
        long first = Math.min(eventA, eventB);
        long second = Math.max(eventA, eventB);
        minWeightsSums.computeIfAbsent(first, k -> new HashMap<>()).put(second, sum);
    }

    private double getMinSum(long eventA, long eventB) {
        long first = Math.min(eventA, eventB);
        long second = Math.max(eventA, eventB);
        return minWeightsSums.getOrDefault(first, Map.of()).getOrDefault(second, 0.0);
    }

    private EventSimilarityAvro mapToEventSimilarityAvro(long eventA, long eventB, double ratio) {
        long first = Math.min(eventA, eventB);
        long second = Math.max(eventA, eventB);
        return EventSimilarityAvro.newBuilder()
                .setEventA(first)
                .setEventB(second)
                .setScore(ratio)
                .setTimestamp(Instant.now())
                .build();
    }
}
