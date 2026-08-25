package org.sopt.poti.domain.notification.repository;

import java.util.List;
import org.sopt.poti.domain.notification.entity.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

  Slice<Notification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

  @Modifying
  @Query("UPDATE Notification n SET n.read = true WHERE n.userId = :userId AND n.read = false")
  void markAllAsReadByUserId(@Param("userId") Long userId);
}
