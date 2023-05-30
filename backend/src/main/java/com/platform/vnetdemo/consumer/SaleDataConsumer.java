package com.platform.vnetdemo.consumer;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.json.JSONObject;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
public class SaleDataConsumer implements MessageListener<String, String> {

    private final WebSocketSession session;

    @SneakyThrows
    @Override
    public void onMessage(ConsumerRecord<String, String> record) {

        String key = record.key();
        String value = record.value();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("entity", key);
        jsonObject.put("data", value);
        jsonObject.put("group", Objects.requireNonNull(session.getUri()).getPath());

        if (session.isOpen()) session.sendMessage(new TextMessage(jsonObject.toString()));
    }
}
