package ru.practicum.stats.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.stats.analyzer.config.KafkaClient;
import ru.practicum.stats.analyzer.handler.SimilarityHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SimilarityProcessor {
    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    private final KafkaClient kafkaClient;
    private final SimilarityHandler similarityHandler;

    public void start() {
        Consumer<Long, SpecificRecordBase> consumer = kafkaClient.getConsumerSimilarity();

        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

        try {
            consumer.subscribe(List.of(kafkaClient.getTopicsProperties().getStatsEventsSimilarityV1()));

            while (true) {
                ConsumerRecords<Long, SpecificRecordBase> records = consumer.poll(kafkaClient.getPollTimeout());

                int count = 0;
                for (ConsumerRecord<Long, SpecificRecordBase> record : records) {
                    handleRecord(record);
                    manageOffsets(record, count, consumer);
                    count++;
                }
                consumer.commitAsync();
            }
        } catch (WakeupException ignored) {

        } catch (Exception e) {
            log.error("Ошибка во время обработки сходств мероприятий: ", e);
        } finally {
            try {
                consumer.commitSync(currentOffsets);
            } finally {
                log.info("Закрываем consumer");
                consumer.close();
            }
        }
    }

    private void manageOffsets(ConsumerRecord<Long, SpecificRecordBase> record, int count,
                               Consumer<Long, SpecificRecordBase> consumer) {
        currentOffsets.put(
                new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset() + 1)
        );

        if (count % 10 == 0) {
            consumer.commitAsync(currentOffsets, (offsets, exception) -> {
                if (exception != null) {
                    log.warn("Ошибка во время фиксации оффсетов: {}", offsets, exception);
                }
            });
        }
    }

    private void handleRecord(ConsumerRecord<Long, SpecificRecordBase> record) {
        if (record.value() instanceof EventSimilarityAvro eventSimilarityAvro) {
            log.info("Пришло сходство мероприятий: eventA = {}, eventB = {}, score = {}",
                    eventSimilarityAvro.getEventA(), eventSimilarityAvro.getEventB(), eventSimilarityAvro.getScore());
            similarityHandler.handle(eventSimilarityAvro);
        }
    }
}
