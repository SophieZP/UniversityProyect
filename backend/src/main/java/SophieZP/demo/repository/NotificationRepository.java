package SophieZP.demo.repository;

import SophieZP.demo.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // Find all notifications for a specific user
    List<Notification> findByUserId(Long userId);
    
    // Find unread notifications for a user
    List<Notification> findByUserIdAndIsReadFalse(Long userId);
    
    // Find notifications by type
    List<Notification> findByUserIdAndType(Long userId, Notification.NotificationType type);
}
