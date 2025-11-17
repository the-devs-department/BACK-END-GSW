package com.gsw.service_tarefa.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient (name = "service-notificacao", url = "http://localhost:8084/notifications")
public interface NotificacaoClient {
  // Fazer a rota aqui e posteriormente trocar as chamadas no service
}
