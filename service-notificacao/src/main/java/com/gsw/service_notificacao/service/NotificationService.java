package com.gsw.service_notificacao.service;


import com.gsw.service_notificacao.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private NotificationRepository notificationRepository;

    /**
     * ...
     * @param userId 
     * ...
     */
    public void sendNotification(String userId, String message, String link) { 
        try {
            
            com.gsw.taskmanager.entity.Notification notification = new com.gsw.taskmanager.entity.Notification(userId, message, link);
            com.gsw.taskmanager.entity.Notification savedNotification = notificationRepository.save(notification);

            messagingTemplate.convertAndSendToUser(
                userId, 
                "/notifications", 
                savedNotification 
            );

        } catch (Exception e) {
            System.err.println("Erro ao enviar notificação: " + e.getMessage());
        }
    }
}