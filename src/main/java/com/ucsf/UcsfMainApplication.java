package com.ucsf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication

public class UcsfMainApplication {

	public static void main(String[] args) {
		SpringApplication.run(UcsfMainApplication.class, args);
	}

}
