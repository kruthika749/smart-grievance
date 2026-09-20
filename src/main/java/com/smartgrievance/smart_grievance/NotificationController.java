package com.smartgrievance.smart_grievance;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(
            NotificationService notificationService) {

        this.notificationService =
                notificationService;
    }

    @GetMapping("/notifications/user/{userId}")
    public List<Notification> getUserNotifications(
            @PathVariable Integer userId) {

        return notificationService
                .getUserNotifications(userId);
    }

    @PutMapping("/notifications/{id}/read")
    public String markAsRead(
            @PathVariable Integer id) {

        notificationService.markAsRead(id);

        return "Notification marked as read.";
    }
}