package com.gsw.service_anexo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ServiceAnexoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceAnexoApplication.class, args);
	}

}
