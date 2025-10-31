package ru.practicum.stats.analyzer.config;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import ru.practicum.kafka.serializer.EventSimilarityDeserializer;
import ru.practicum.kafka.serializer.UserActionDeserializer;

import java.time.Duration;
import java.util.Properties;

@Configuration
public class KafkaConfig {
    @Value("${kafka.bootstrap.servers}")
    private String bootstrapServers;
    @Value("${kafka.consumer.group.action}")
    private String groupAction;

    @Value("${kafka.consumer.group.similarity}")
    private String groupSimilarity;

    @Value("${kafka.consumer.poll.timeout}")
    private long pollTimeout;

    @Autowired
    private KafkaTopicsProperties topicsProperties;

    private KafkaConsumer<Long, SpecificRecordBase> kafkaConsumerAction() {
        Properties config = new Properties();
        config.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getCanonicalName());
        config.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, UserActionDeserializer.class.getCanonicalName());
        config.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupAction);
        return new KafkaConsumer<>(config);
    }

    private KafkaConsumer<Long, SpecificRecordBase> kafkaConsumerSimilarity() {
        Properties config = new Properties();
        config.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getCanonicalName());
        config.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, EventSimilarityDeserializer.class.getCanonicalName());
        config.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupSimilarity);
        return new KafkaConsumer<>(config);
    }

    @Bean
    @Scope("prototype")
    public KafkaClient getClient() {
        return new KafkaClient() {

            private Consumer<Long, SpecificRecordBase> consumerAction;
            private Consumer<Long, SpecificRecordBase> consumerSimilarity;

            @Override
            public Consumer<Long, SpecificRecordBase> getConsumerAction() {
                if (consumerAction == null) {
                    consumerAction = kafkaConsumerAction();
                }
                return consumerAction;
            }

            @Override
            public Consumer<Long, SpecificRecordBase> getConsumerSimilarity() {
                if (consumerSimilarity == null) {
                    consumerSimilarity = kafkaConsumerSimilarity();
                }
                return consumerSimilarity;
            }

            @Override
            public Duration getPollTimeout() {
                return Duration.ofMillis(pollTimeout);
            }

            @Override
            public KafkaTopicsProperties getTopicsProperties() {
                return topicsProperties;
            }

        };
    }
}
