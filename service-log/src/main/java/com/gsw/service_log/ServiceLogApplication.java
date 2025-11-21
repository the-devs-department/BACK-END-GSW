package com.gsw.service_log;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ServiceLogApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceLogApplication.class, args);
	}

}
