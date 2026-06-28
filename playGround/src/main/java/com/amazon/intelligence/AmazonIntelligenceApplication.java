package com.amazon.intelligence;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AmazonIntelligenceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AmazonIntelligenceApplication.class, args);
    }
}
