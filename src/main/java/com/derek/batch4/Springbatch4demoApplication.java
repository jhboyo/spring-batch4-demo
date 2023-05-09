package com.derek.batch4;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing
@SpringBootApplication
public class Springbatch4demoApplication {

	public static void main(String[] args) {
		SpringApplication.run(Springbatch4demoApplication.class, args);
	}

}
