package oss.jmarsic.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import oss.jmarsic.app.model.Notification;
import oss.jmarsic.app.model.User;
import oss.jmarsic.app.model.UserNotification;
import oss.jmarsic.app.repository.NotificationRepository;
import oss.jmarsic.app.repository.UserNotificationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserNotificationRepository userNotificationRepository;

    @Autowired
    private UserService userService;

    public void sendNotification(String message) {
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setSentAt(LocalDateTime.now());
        notificationRepository.save(notification);

        List<User> users = userService.findAll();
        for (User user : users) {
            UserNotification userNotification = new UserNotification();
            userNotification.setUser(user);
            userNotification.setNotification(notification);
            userNotificationRepository.save(userNotification);
        }
    }

    public Notification getLatestNotification() {
        return notificationRepository.findTopByOrderBySentAtDesc();
    }

    public void markAsRead(UUID notificationId, User user) {
        UserNotification userNotification = userNotificationRepository.findByUserIdAndNotificationId(user.getId(), notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found for this user."));
        userNotification.setRead(true);
        userNotificationRepository.save(userNotification);
    }
}
