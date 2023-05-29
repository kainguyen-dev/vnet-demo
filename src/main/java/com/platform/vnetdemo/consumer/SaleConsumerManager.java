package com.platform.vnetdemo.consumer;

import com.platform.vnetdemo.properties.PlatformProperties;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.stereotype.Component;

@Component
public class SaleConsumerManager {

    private final ConcurrentKafkaListenerContainerFactory<String, String> consumerFactory;

    public SaleConsumerManager(ConcurrentKafkaListenerContainerFactory<String, String> consumerFactory,
                               PlatformProperties platformProperties) {
        this.consumerFactory = consumerFactory;

        platformProperties.getStream().forEach((stream, config) -> {
            createConsumer(config.getTopic());
        });

    }


    public void createConsumer(String topic) {
        ConcurrentMessageListenerContainer<String, String> container = consumerFactory.createContainer(topic);
        container.setupMessageListener(new SaleDataConsumer());
        container.start();
    }

}
