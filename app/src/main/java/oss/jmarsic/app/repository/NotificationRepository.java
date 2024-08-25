package oss.jmarsic.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import oss.jmarsic.app.model.Notification;

import java.util.Optional;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
}
