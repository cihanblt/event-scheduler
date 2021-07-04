package com.n11.eventscheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.n11.eventscheduler.repositories")
public class EventSchedulerApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventSchedulerApplication.class, args);
	}



}
