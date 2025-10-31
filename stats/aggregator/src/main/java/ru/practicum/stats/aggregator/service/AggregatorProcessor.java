package ru.practicum.stats.aggregator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.stats.aggregator.config.KafkaClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregatorProcessor {

    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    private final KafkaClient kafkaClient;
    private final SimilarityCalculator calculator;


    public void start() {
        Consumer<Long, SpecificRecordBase> consumer = kafkaClient.getConsumer();
        Producer<Long, SpecificRecordBase> producer = kafkaClient.getProducer();

        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

        try {
            consumer.subscribe(List.of(kafkaClient.getTopicsProperties().getStatsUserActionV1()));

            while (true) {
                ConsumerRecords<Long, SpecificRecordBase> records = consumer.poll(kafkaClient.getPollTimeout());

                int count = 0;
                for (ConsumerRecord<Long, SpecificRecordBase> record : records) {
                    handleRecord(record, producer);
                    manageOffsets(record, count, consumer);
                    count++;
                }
                consumer.commitAsync();
            }
        } catch (WakeupException ignored) {

        } catch (Exception e) {
            log.error("Ошибка во время обработки действий пользователя", e);
        } finally {
            try {
                producer.flush();
                consumer.commitSync(currentOffsets);
            } finally {
                log.info("Закрываем consumer");
                consumer.close();
                log.info("Закрываем producer");
                producer.close();
            }
        }
    }

    private void manageOffsets(ConsumerRecord<Long, SpecificRecordBase> record, int count, Consumer<Long, SpecificRecordBase> consumer) {
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

    private void handleRecord(ConsumerRecord<Long, SpecificRecordBase> record, Producer<Long, SpecificRecordBase> producer) {
        if (record.value() instanceof UserActionAvro action) {
            List<EventSimilarityAvro> similarities = calculator.calculateSimilarity(action);
            if (similarities.isEmpty()) return;

            for (EventSimilarityAvro similarity : similarities) {
                long timestamp = similarity.getTimestamp().toEpochMilli();
                ProducerRecord<Long, SpecificRecordBase> similarityRecord = new ProducerRecord<>(
                        kafkaClient.getTopicsProperties().getStatsEventsSimilarityV1(),
                        null,
                        timestamp,
                        similarity.getEventA(),
                        similarity
                );

                producer.send(similarityRecord, (metadata, ex) -> {
                    if (ex != null) {
                        log.error("Ошибка при отправке сходства мероприятий для мероприятий eventA = {} и eventB = {}: {}",
                                similarity.getEventA(), similarity.getEventB(), ex.getMessage(), ex);
                    } else {
                        log.info("Сходство мероприятий  eventA = {} и eventB = {} успешно отправлено: partition={}, offset={}",
                                similarity.getEventA(), similarity.getEventB(), metadata.partition(), metadata.offset());
                    }
                });
            }
        } else {
            log.warn("Получено неизвестное сообщение: {}", record.value());
        }
    }
}
