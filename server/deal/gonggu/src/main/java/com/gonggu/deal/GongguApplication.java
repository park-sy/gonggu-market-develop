package com.gonggu.deal;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.util.TimeZone;
@EnableAsync
@EnableBatchProcessing
//@EnableScheduling
@SpringBootApplication
public class GongguApplication {

	public static void main(String[] args) {
		SpringApplication.run(GongguApplication.class, args);
	}

}
