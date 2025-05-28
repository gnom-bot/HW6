package org.example.notification.consumer;

import org.example.notification.dto.UserEventDTO;
import org.example.notification.service.EmailService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UserEventListener {

    private final EmailService emailService;

    public UserEventListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "user-events", groupId = "notification-group", containerFactory = "kafkaListenerContainerFactory")
    public void handleUserEvent(UserEventDTO event) {
        String message;
        if ("created".equalsIgnoreCase(event.getOperation())) {
            message = "Здравствуйте! Ваш аккаунт на сайте был успешно создан.";
        } else if ("deleted".equalsIgnoreCase(event.getOperation())) {
            message = "Здравствуйте! Ваш аккаунт был удалён.";
        } else {
            return;
        }
        emailService.sendEmail(event.getEmail(), "Уведомление", message);
    }
}