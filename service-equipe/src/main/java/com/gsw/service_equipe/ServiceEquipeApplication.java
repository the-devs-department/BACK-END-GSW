package com.gsw.service_equipe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.gsw.service_equipe.client")
public class ServiceEquipeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceEquipeApplication.class, args);
    }

}
