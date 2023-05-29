package com.platform.vnetdemo.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "platform")
@Data
public class PlatformProperties {

    private Input input;
    private Kafka kafka;
    private Map<String, StreamProperties> stream;

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
