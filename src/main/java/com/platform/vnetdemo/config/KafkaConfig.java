package com.platform.vnetdemo.config;


import com.fasterxml.jackson.databind.JsonNode;
import com.platform.vnetdemo.properties.PlatformProperties;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.connect.json.JsonDeserializer;
import org.apache.kafka.connect.json.JsonSerializer;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;
import java.util.Random;

@Configuration
public class KafkaConfig {
    @Bean
    public NewTopic saleReportTopic(PlatformProperties properties) {
        return new NewTopic(properties.getKafka().getTopic(), 1, (short) 1);
    }
    @Bean
    public Properties kafkaProperties() {
        final var props = new Properties();
        props.setProperty(ProducerConfig.CLIENT_ID_CONFIG, "platform-producer");
        props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return props;
    }

    @Bean
    public Properties streamProperties() {
        final Serde<JsonNode> jsonSerde = Serdes.serdeFrom(new JsonSerializer(), new JsonDeserializer());
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "STREAM_JSON_POC_" + new Random().nextInt());
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, jsonSerde.getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, jsonSerde.getClass());
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 5000L);
        return props;
    }

    @Bean
    public KafkaProducer<String, String> kafkaProducer(Properties kafkaProperties) {
        return new KafkaProducer<>(kafkaProperties);
    }

    @Bean
    public Admin kafkaAdmin(Properties kafkaProperties) {
        return Admin.create(kafkaProperties);
    }


}
