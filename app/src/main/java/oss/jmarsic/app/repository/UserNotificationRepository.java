package oss.jmarsic.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import oss.jmarsic.app.model.UserNotification;

import java.util.Optional;
import java.util.UUID;

public interface UserNotificationRepository extends JpaRepository<UserNotification, UUID> {
    Optional<UserNotification> findByUserIdAndNotificationId(UUID userId, UUID notificationId);
}
