package com.gsw.service_tarefa.client;

import com.gsw.service_tarefa.dto.log.LogRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "service-log", url = "http://localhost:8082/logs") //porta do log para que informações de tarefa sejam enviadas ao serviço de log
public interface LogClient {

    @PostMapping
    void registrarLog(LogRequest logRequest);
}
