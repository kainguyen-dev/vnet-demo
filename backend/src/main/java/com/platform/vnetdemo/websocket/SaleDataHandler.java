package com.platform.vnetdemo.websocket;

import com.platform.vnetdemo.consumer.SaleConsumerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@RequiredArgsConstructor
public class SaleDataHandler extends TextWebSocketHandler {

    private final SaleConsumerFactory saleConsumerFactory;
    private final String topic;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        saleConsumerFactory.createConsumer(session, topic);
    }

}
