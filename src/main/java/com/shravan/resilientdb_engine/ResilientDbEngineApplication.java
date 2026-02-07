package com.shravan.resilientdb_engine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ResilientDbEngineApplication {

	public static void main(String[] args) {
		SpringApplication.run(ResilientDbEngineApplication.class, args);
	}

}
