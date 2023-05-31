package com.platform.vnetdemo.websocket;

import com.platform.vnetdemo.consumer.SaleConsumerFactory;
import com.platform.vnetdemo.properties.PlatformProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    SaleConsumerFactory manager;

    @Autowired
    PlatformProperties properties;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {

        properties.getStream().forEach((streamName, streamProperties) ->
            webSocketHandlerRegistry.addHandler(new SaleDataHandler(manager, streamProperties.getTopic()),
                    "/" + streamName)
                .setAllowedOrigins("*"));

    }

}
