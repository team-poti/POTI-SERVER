package org.sopt.poti.domain.notification.repository;

import org.sopt.poti.domain.notification.entity.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

  Slice<Notification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}
