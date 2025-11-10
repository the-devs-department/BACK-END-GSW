package com.gsw.service_usuario;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ServiceUsuarioApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceUsuarioApplication.class, args);
	}

}
