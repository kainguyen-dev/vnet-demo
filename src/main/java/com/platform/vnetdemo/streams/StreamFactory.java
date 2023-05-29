package com.platform.vnetdemo.streams;

import com.platform.vnetdemo.properties.PlatformProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
@Slf4j
public class StreamFactory {

    private final PlatformProperties properties;
    private final Admin kafkaAdmin;

    public StreamFactory(PlatformProperties properties,
                         Admin kafkaAdmin) {
        this.properties = properties;
        this.kafkaAdmin = kafkaAdmin;

        buildTopic();
    }

    public void buildTopic() {
        List<NewTopic> topicList = new ArrayList<>();
        properties.getStream()
            .forEach((key, value) -> topicList.add(new NewTopic(value.getTopic(), 1, (short) 1)));
        try {
            kafkaAdmin.createTopics(topicList).all().get();
            log.info("Create topics [{}] completed ! ", topicList);
        } catch (ExecutionException | InterruptedException e) {
            log.error(e.getMessage(), e.getCause());
        }
    }

}
