package com.platform.vnetdemo.streams;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.vnetdemo.properties.PlatformProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.connect.json.JsonDeserializer;
import org.apache.kafka.connect.json.JsonSerializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Component
@Slf4j
public class StreamFactory implements DisposableBean {

    private final PlatformProperties platformProperties;
    private final Admin kafkaAdmin;
    List<KafkaStreams> streamsList;

    public StreamFactory(PlatformProperties platformProperties,
                         Admin kafkaAdmin) {
        this.platformProperties = platformProperties;
        this.kafkaAdmin = kafkaAdmin;
        this.streamsList = new ArrayList<>();

        clearTopic();
        buildTopic();
        buildStreams();
    }

    public void clearTopic() {
        List<String> topicList = new ArrayList<>();
        platformProperties.getStream()
            .forEach((key, value) -> topicList.add(value.getTopic()));
        try {
            kafkaAdmin.deleteTopics(topicList).all().get();
            log.info("Remove topics [{}] completed ! ", topicList);
        } catch (ExecutionException | InterruptedException e) {
            log.error(e.getMessage(), e.getCause());
        }
    }


    public void buildTopic() {
        List<NewTopic> topicList = new ArrayList<>();
        platformProperties.getStream()
            .forEach((key, value) -> topicList.add(new NewTopic(value.getTopic(), 1, (short) 1)));
        try {
            kafkaAdmin.createTopics(topicList).all().get();
            log.info("Create topics [{}] completed ! ", topicList);
        } catch (ExecutionException | InterruptedException e) {
            log.error(e.getMessage(), e.getCause());
        }
    }

    public void buildStreams() {
        final Serde<JsonNode> jsonSerde = Serdes.serdeFrom(new JsonSerializer(), new JsonDeserializer());
        ObjectMapper objectMapper = new ObjectMapper();
        final StreamsBuilder builder = new StreamsBuilder();

        platformProperties.getStream()
            .forEach((streamName, streamConfig) -> {

                    Properties props = new Properties();
                    props.put(StreamsConfig.APPLICATION_ID_CONFIG, "STREAM_JSON_POC_" + new Random().nextInt());
                    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
                    props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, jsonSerde.getClass());
                    props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, jsonSerde.getClass());
                    props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 5000L);

                    KStream<String, JsonNode> inputStream = builder.stream(
                        platformProperties.getKafka().getTopic(),
                        Consumed.with(Serdes.String(), jsonSerde)
                    );

                    if (streamConfig.getLog()) {
                        inputStream.foreach((s, value) -> log.info("Receive Incoming rec {}", value.toPrettyString()));
                    }

                    inputStream
                        .map((k, v) -> {
                            Map<String, Integer> map = buildMapFromJsonNode(v, streamConfig.getSumField());
                            String jsonString = "";
                            try {
                                jsonString = objectMapper.writeValueAsString(map);
                            } catch (JsonProcessingException e) {
                                log.error(e.getMessage(), e);
                            }
                            return new KeyValue<>(v.get(streamConfig.getGroupKey()).asText(), jsonString);
                        })
                        .groupByKey(Grouped.with(Serdes.String(), Serdes.String()))
                        .aggregate(
                            () -> "{}",
                            (key, value, aggregate) -> {
                                try {
                                    Map<String, Integer> aggregateMap = objectMapper.readValue(aggregate, new TypeReference<>() {
                                    });
                                    Map<String, Integer> valueMap = objectMapper.readValue(value, new TypeReference<>() {
                                    });

                                    for (String field : streamConfig.getSumField()) {
                                        aggregateMap.put(field, aggregateMap.getOrDefault(field, 0) + valueMap.getOrDefault(field, 0));
                                    }

                                    return objectMapper.writeValueAsString(aggregateMap);
                                } catch (IOException e) {
                                    log.error(e.getMessage(), e);
                                }
                                return "{}";
                            },
                            Materialized.with(Serdes.String(), Serdes.String())
                        )
                        .toStream()
                        .to(streamConfig.getTopic(), Produced.with(Serdes.String(), Serdes.String()));

                    KafkaStreams stream = new KafkaStreams(builder.build(), props);
                    streamsList.add(stream);
                }
            );

        streamsList.forEach(KafkaStreams::start);
        log.info(" Kafka streams start ! ");
    }

    @Override
    public void destroy() {
        try {
            streamsList.forEach(KafkaStreams::cleanUp);
            streamsList.forEach(KafkaStreams::close);
            log.info(" Kafka streams clean up completed ! ");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public Map<String, Integer> buildMapFromJsonNode(JsonNode json, String[] fields) {
        Map<String, Integer> map = new HashMap<>();
        for (String field : fields) {
            map.put(field, json.get(field).asInt());
        }
        return map;
    }

}
