package com.platform.vnetdemo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;

@SpringBootApplication(exclude = KafkaAutoConfiguration.class)
@Slf4j
public class VnetDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(VnetDemoApplication.class, args);
    }

}
