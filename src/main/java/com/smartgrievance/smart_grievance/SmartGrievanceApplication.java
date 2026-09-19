package com.smartgrievance.smart_grievance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SmartGrievanceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartGrievanceApplication.class, args);
    }
}
