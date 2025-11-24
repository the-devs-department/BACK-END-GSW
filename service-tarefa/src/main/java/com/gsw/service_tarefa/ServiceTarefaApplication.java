package com.gsw.service_tarefa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.gsw.service_tarefa.client")
public class ServiceTarefaApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceTarefaApplication.class, args);
    }
}



