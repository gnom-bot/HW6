package org.example.notification.controller;

import org.example.notification.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send")
    public void sendEmail(@RequestParam String email, @RequestParam String operation) {
        String message = switch (operation) {
            case "created" -> "Здравствуйте! Ваш аккаунт на сайте был успешно создан.";
            case "deleted" -> "Здравствуйте! Ваш аккаунт был удалён.";
            default -> throw new IllegalArgumentException("Неизвестная операция");
        };
        emailService.sendEmail(email, "Уведомление", message);
    }
}