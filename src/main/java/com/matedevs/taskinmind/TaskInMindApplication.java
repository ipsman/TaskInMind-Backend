package com.matedevs.taskinmind;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TaskInMindApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskInMindApplication.class, args);
    }

}
