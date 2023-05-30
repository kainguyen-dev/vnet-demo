package com.platform.vnetdemo.config;


import com.platform.vnetdemo.properties.CloudKafkaProperties;
import com.platform.vnetdemo.properties.PlatformProperties;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

import java.util.Properties;

@Configuration
@EnableKafka
public class KafkaConfig {
    @Bean
    public NewTopic saleReportTopic(PlatformProperties properties) {
        return new NewTopic(properties.getKafka().getTopic(), 1, (short) 1);
    }

    @Bean
    public Properties kafkaProperties(CloudKafkaProperties properties) {
        final var props = new Properties();
        props.setProperty(ProducerConfig.CLIENT_ID_CONFIG, "platform-producer");
        props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getProperties().getBootstrap().getServers());
        props.setProperty("security.protocol", properties.getProperties().getSecurity().getProtocol());
        props.setProperty("sasl.jaas.config", properties.getProperties().getSasl().getJaas().getConfig());
        props.setProperty("sasl.mechanism", properties.getProperties().getSasl().getMechanism());
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


    /**

     public ConsumerFactory<String, String> consumerFactory() {
     Map<String, Object> props = new HashMap<>();
     props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
     props.put(ConsumerConfig.GROUP_ID_CONFIG, "sale-consumer");
     props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
     props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
     return new DefaultKafkaConsumerFactory<>(props);
     }


     public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
     ConcurrentKafkaListenerContainerFactory<String, String> factory =
     new ConcurrentKafkaListenerContainerFactory<>();
     factory.setConsumerFactory(consumerFactory());
     return factory;
     }

     */

}
