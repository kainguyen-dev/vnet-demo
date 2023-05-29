package com.platform.vnetdemo.config;


import com.platform.vnetdemo.properties.PlatformProperties;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KafkaConfig {
    @Bean
    public NewTopic saleReportTopic(PlatformProperties properties) {
        return new NewTopic(properties.getKafka().getTopic(), 1, (short) 1);
    }

    @Bean
    public NewTopic sumData(PlatformProperties properties) {
        return new NewTopic("SALE_OUTPUT", 1, (short) 1);
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
    public KafkaProducer<String, String> kafkaProducer(Properties kafkaProperties) {
        return new KafkaProducer<>(kafkaProperties);
    }

    @Bean
    public Admin kafkaAdmin(Properties kafkaProperties) {
        return Admin.create(kafkaProperties);
    }


}
