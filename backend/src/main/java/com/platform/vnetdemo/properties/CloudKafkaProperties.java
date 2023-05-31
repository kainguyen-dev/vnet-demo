package com.platform.vnetdemo.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("spring.kafka")
@Data
public class CloudKafkaProperties {
    private Properties properties = new Properties();

    @Data
    public static class Properties {
        private Sasl sasl = new Sasl();
        private Session session = new Session();
        private Security security = new Security();
        private Bootstrap bootstrap = new Bootstrap();
    }

    @Data
    public static class Sasl {
        private Jaas jaas = new Jaas();
        private String mechanism;
    }

    @Data
    public static class Jaas {
        private String config;
    }

    @Data
    public static class Session {
        private Timeout timeout = new Timeout();
    }

    @Data
    public static class Timeout {
        private String ms;
    }

    @Data
    public static class Security {
        private String protocol;
    }

    @Data
    public static class Bootstrap {
        private String servers;
    }
}
