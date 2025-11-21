package com.gsw.service_tarefa.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRequestDTO {
  private String userId;
  private String message;
  private String link;
}
