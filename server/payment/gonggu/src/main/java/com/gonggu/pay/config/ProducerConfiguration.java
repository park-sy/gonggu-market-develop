package com.gonggu.pay.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class ProducerConfiguration {
    @Value("${spring.kafka.bootstrap-servers}")
    private String servers;

    @Bean
    public ProducerFactory<String, Object> producerConfigurations() {
        Map<String, Object> configurations = new HashMap<>();
        configurations.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        configurations.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configurations.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configurations);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerConfigurations());
    }
}