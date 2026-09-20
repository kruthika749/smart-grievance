package com.smartgrievance.smart_grievance;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(
            NotificationRepository notificationRepository) {

        this.notificationRepository =
                notificationRepository;
    }

    public void createNotification(
            Integer userId,
            String title,
            String message,
            String type) {

        if (userId == null) {
            return;
        }

        Notification notification =
                new Notification();

        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setReadStatus(false);
        notification.setCreatedAt(
                LocalDateTime.now()
        );

        notificationRepository.save(
                notification
        );
    }

    public List<Notification> getUserNotifications(
            Integer userId) {

        return notificationRepository
                .findByUserIdOrderByCreatedAtDesc(
                        userId
                );
    }

    public void markAsRead(Integer notificationId) {

        notificationRepository
                .findById(notificationId)
                .ifPresent(notification -> {

                    notification.setReadStatus(true);

                    notificationRepository.save(
                            notification
                    );
                });
    }
}