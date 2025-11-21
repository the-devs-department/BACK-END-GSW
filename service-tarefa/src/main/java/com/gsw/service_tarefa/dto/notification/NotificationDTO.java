package com.gsw.service_tarefa.dto.notification;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {
    private String id;

    private String userId;

    private String message; 

    private String link; 

    private boolean read = false;

    private LocalDateTime createdAt = LocalDateTime.now();
}
