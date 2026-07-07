package com.travelai.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"com.travelai"})
@EntityScan(basePackages = {"com.travelai.user.entity", "com.travelai.trip.entity", "com.travelai.chat.entity"})
@EnableJpaRepositories(basePackages = {"com.travelai.user.repository", "com.travelai.trip.repository", "com.travelai.chat.repository"})
public class TravelAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TravelAiApplication.class, args);
    }
}