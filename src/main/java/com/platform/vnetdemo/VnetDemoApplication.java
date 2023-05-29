package com.platform.vnetdemo;

import com.fasterxml.jackson.databind.JsonNode;
import com.platform.vnetdemo.properties.PlatformProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.connect.json.JsonDeserializer;
import org.apache.kafka.connect.json.JsonSerializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Properties;
import java.util.Random;

@SpringBootApplication
@Slf4j
public class VnetDemoApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(VnetDemoApplication.class, args);
        PlatformProperties properties = context.getBean(PlatformProperties.class);
        log.info(properties.toString());


        final Serde<String> stringSerde = Serdes.String();
        final Serde<JsonNode> jsonSerde = Serdes.serdeFrom(new JsonSerializer(), new JsonDeserializer());
        final Serde<Integer> integerSerde = Serdes.Integer();

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "STREAM_JSON_POC_" + new Random().nextInt());
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, jsonSerde.getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, jsonSerde.getClass());
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 5000L);

        final StreamsBuilder builder = new StreamsBuilder();
        KStream<String, JsonNode> inputStream = builder.stream(properties.getKafka().getTopic(),
            Consumed.with(Serdes.String(), jsonSerde));

        inputStream.foreach((s, value) -> {
            System.out.println("Receive Incoming rec " + value.toPrettyString());
            System.out.println("Get sale store " + value.get("StoreName"));
            System.out.println();
        });

        inputStream
            .map((k, v) -> {
                System.out.println(v.get("StoreName").asText());
                System.out.println(v.get("SalesUnits").asInt());

                return new KeyValue<>(v.get("StoreName").asText(), v.get("SalesUnits").asInt());
            })
            .groupByKey(Grouped.with(Serdes.String(), Serdes.Integer()))
            .aggregate(
                () -> 0,
                (key, value, aggregate) -> aggregate + value,
                Materialized.<String, Integer, KeyValueStore<Bytes, byte[]>>as("aggregate-store")
                    .withKeySerde(Serdes.String())
                    .withValueSerde(Serdes.Integer())
            )
            .toStream()
            .mapValues(v -> v.toString() + " total sales")
            .to("SALE_OUTPUT", Produced.with(Serdes.String(), Serdes.String()));

        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();
        System.out.println("Stream start !");


    }

}
