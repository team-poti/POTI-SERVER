package org.sopt.poti.domain.notification.dto.response;

import java.time.LocalDateTime;
import org.sopt.poti.domain.notification.entity.Notification;
import org.sopt.poti.domain.notification.entity.NotificationType;

public record NotificationResponse(
    Long id,
    String title,
    String body,
    NotificationType type,
    String deeplink,
    boolean read,
    LocalDateTime createdAt
) {
  public static NotificationResponse from(Notification notification) {
    return new NotificationResponse(
        notification.getId(),
        notification.getTitle(),
        notification.getBody(),
        notification.getType(),
        notification.getDeeplink(),
        notification.isRead(),  // isRead() = getter of field "read"
        notification.getCreatedAt()
    );
  }
}
