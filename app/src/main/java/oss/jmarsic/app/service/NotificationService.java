package oss.jmarsic.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import oss.jmarsic.app.model.Notification;
import oss.jmarsic.app.repository.NotificationRepository;

import java.time.LocalDateTime;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    public void sendNotification(String message) {
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setSentAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    public Notification getLatestNotification() {
        return notificationRepository.findTopByOrderBySentAtDesc();
    }
}
