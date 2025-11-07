package fr.cop1.studentpantrybackend.repositories;

import fr.cop1.studentpantrybackend.entities.Notification;
import fr.cop1.studentpantrybackend.entities.User;
import fr.cop1.studentpantrybackend.enums.NotificationStatus;
import fr.cop1.studentpantrybackend.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUser(User user);
    List<Notification> findByUserOrderBySentAtDesc(User user);
    List<Notification> findByType(NotificationType type);
    List<Notification> findByStatus(NotificationStatus status);
    List<Notification> findByUserAndStatus(User user, NotificationStatus status);
    List<Notification> findByUserAndType(User user, NotificationType type);

    // Notifications non lues
    List<Notification> findByUserAndStatusOrderBySentAtDesc(User user, NotificationStatus status);
    Long countByUserAndStatus(User user, NotificationStatus status);

    // Notifications récentes
    List<Notification> findBySentAtAfter(LocalDateTime dateTime);

    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.status = fr.cop1.studentpantrybackend.enums.NotificationStatus.READ, n.readAt = CURRENT_TIMESTAMP WHERE n.user = :user AND n.status = fr.cop1.studentpantrybackend.enums.NotificationStatus.SENT")
    void markAllAsRead(@Param("user") User user);
}
