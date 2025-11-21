package com.gsw.service_tarefa.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.gsw.service_tarefa.dto.notification.NotificationDTO;
import com.gsw.service_tarefa.dto.notification.NotificationRequestDTO;

@FeignClient (name = "service-notificacao", url = "http://localhost:8084/notifications")
public interface NotificacaoClient {
  @PostMapping("/send")
  ResponseEntity<NotificationDTO> createNotification(@RequestBody NotificationRequestDTO request);
}
