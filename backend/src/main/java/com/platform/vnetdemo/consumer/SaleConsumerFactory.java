package com.platform.vnetdemo.consumer;

import com.platform.vnetdemo.properties.CloudKafkaProperties;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class SaleConsumerFactory {

    private final CloudKafkaProperties cloudKafkaProperties;

    public void createConsumer(WebSocketSession session, String topic) {

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, cloudKafkaProperties.getProperties().getBootstrap().getServers());
        props.put(StreamsConfig.SECURITY_PROTOCOL_CONFIG, cloudKafkaProperties.getProperties().getSecurity().getProtocol());
        props.put(SaslConfigs.SASL_MECHANISM, cloudKafkaProperties.getProperties().getSasl().getMechanism());
        props.put(SaslConfigs.SASL_JAAS_CONFIG, cloudKafkaProperties.getProperties().getSasl().getJaas().getConfig());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "sale-consumer-" + new Random().nextInt());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        ConsumerFactory<String, String> consumerFactory = new DefaultKafkaConsumerFactory<>(props);

        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);

        ConcurrentMessageListenerContainer<String, String> container = factory.createContainer(topic);
        container.setupMessageListener(new SaleDataConsumer(session));
        container.start();

    }

}
