package com.gsw.service_notificacao.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data 
@NoArgsConstructor 
@Document(collection = "notifications") 
public class Notification {

    @Id
    private String id;

    private String userId;

    private String message; 

    private String link; 

    private boolean read = false;

    private LocalDateTime createdAt = LocalDateTime.now();

    public Notification(String userId, String message, String link) {
        this.userId = userId; 
        this.message = message;
        this.link = link;
    }
}