package com.platform.vnetdemo.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "platform")
@Data
public class PlatformProperties {

    private Input input;
    private Kafka kafka;

    @Data
    static public class Input {
        String[] filePaths;
        int interval;
    }

    @Data
    static public class Kafka {
        String topic;
    }

}
