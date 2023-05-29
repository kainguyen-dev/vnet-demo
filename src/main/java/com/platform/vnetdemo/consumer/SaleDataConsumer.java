package com.platform.vnetdemo.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.MessageListener;

public class SaleDataConsumer implements MessageListener<String, String> {

    @Override
    public void onMessage(ConsumerRecord<String, String> record) {
        String key = record.key();
        String value = record.value();
        int partition = record.partition();
        long offset = record.offset();


        System.out.println();
        System.out.println("TOPIC " + record.topic());
        System.out.println("Received message:");
        System.out.println("Key: " + key);
        System.out.println("Value: " + value);
        System.out.println("Partition: " + partition);
        System.out.println("Offset: " + offset);
        System.out.println();
    }
}
