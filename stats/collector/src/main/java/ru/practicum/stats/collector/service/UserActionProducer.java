package ru.practicum.stats.collector.service;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.stats.collector.config.KafkaTopicsProperties;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class UserActionProducer {
    private final Producer<Long, SpecificRecordBase> producer;
    private final KafkaTopicsProperties kafkaTopics;

    private void send(String topic, SpecificRecordBase event, long timestamp, Long key) {
        ProducerRecord<Long, SpecificRecordBase> producerRecord = new ProducerRecord<>(
                topic, null, timestamp, key, event);
        producer.send(producerRecord);
    }

    public void sendUserAction(SpecificRecordBase userAction) {
        UserActionAvro avroAction = (UserActionAvro) userAction;
        Long userId = avroAction.getUserId();
        long timestamp = avroAction.getTimestamp().toEpochMilli();
        send(kafkaTopics.getStatsUserActionV1(), avroAction, timestamp, userId);
    }

    @PreDestroy
    public void stop() {
        producer.flush();
        producer.close(Duration.ofSeconds(30));
    }

}
