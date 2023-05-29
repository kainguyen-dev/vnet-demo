package com.platform.vnetdemo.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.vnetdemo.properties.PlatformProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class SaleDataProducer {

    private final ScheduledExecutorService executor;
    private final PlatformProperties properties;
    private final AtomicInteger fileIndex;
    private final KafkaProducer<String, String> kafkaProducer;

    public SaleDataProducer(PlatformProperties properties,
                            KafkaProducer<String, String> kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
        this.executor = Executors.newSingleThreadScheduledExecutor();
        this.properties = properties;
        this.fileIndex = new AtomicInteger();

        this.execute();
    }

    public List<String> readData() {

        List<String> result = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        String filePath = this.properties.getInput().getFilePaths()[this.fileIndex.get()];
        Map<Integer, String> headers = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\\|");
                if (headers == null) {
                    headers = new HashMap<>();
                    for (int i = 0; i < data.length; i++) {
                        headers.put(i, data[i].replace("\"", ""));
                    }
                } else {
                    Map<String, Object> json = new HashMap<>();
                    for (int i = 0; i < data.length; i++) {
                        json.put(headers.get(i), data[i]);
                    }
                    String jsonString = objectMapper.valueToTree(json).toString();
                    result.add(jsonString);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileIndex.incrementAndGet();
        if (fileIndex.get() == this.properties.getInput().getFilePaths().length) fileIndex.set(0);
        return result;
    }

    public void sendData(List<String> data) {
        data.forEach(json -> {
            final var message = new ProducerRecord<>(
                properties.getKafka().getTopic(),
                "key_data",
                json
            );
            kafkaProducer.send(message);
        });
        log.info("Complete send data total [" + data.size() + "] items");
    }

    public void execute() {
        this.executor.scheduleAtFixedRate(() -> sendData(readData()),
            0L,
            properties.getInput().getInterval(),
            TimeUnit.SECONDS);
    }

}
