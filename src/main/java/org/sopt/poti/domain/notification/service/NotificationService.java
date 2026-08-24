package org.sopt.poti.domain.notification.service;

import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.notification.dto.request.NotificationSettingRequest;
import org.sopt.poti.domain.notification.dto.response.NotificationResponse;
import org.sopt.poti.domain.notification.dto.response.NotificationSettingResponse;
import org.sopt.poti.domain.notification.entity.Notification;
import org.sopt.poti.domain.notification.entity.NotificationType;
import org.sopt.poti.domain.notification.repository.NotificationRepository;
import org.sopt.poti.domain.user.entity.User;
import org.sopt.poti.domain.user.repository.UserRepository;
import org.sopt.poti.global.error.BusinessException;
import org.sopt.poti.global.error.ErrorStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

  private final NotificationRepository notificationRepository;
  private final UserRepository userRepository;

  @Transactional
  public Long save(Long userId, String title, String body, NotificationType type, String deeplink) {
    return notificationRepository.save(Notification.builder()
        .userId(userId)
        .title(title)
        .body(body)
        .type(type)
        .deeplink(deeplink)
        .build()).getId();
  }

  @Transactional(readOnly = true)
  public Slice<NotificationResponse> getNotifications(Long userId, Pageable pageable) {
    return notificationRepository
        .findByUserIdOrderByCreatedAtDesc(userId, pageable)
        .map(NotificationResponse::from);
  }

  @Transactional(readOnly = true)
  public NotificationSettingResponse getSettings(Long userId) {
    User user = getUser(userId);
    return NotificationSettingResponse.from(user);
  }

  @Transactional
  public NotificationSettingResponse updateSettings(Long userId, NotificationSettingRequest request) {
    User user = getUser(userId);
    user.updateNotificationSettings(request.tradeNotificationEnabled(), request.eventNotificationEnabled());
    return NotificationSettingResponse.from(user);
  }

  @Transactional
  public void readNotification(Long userId, Long notificationId) {
    Notification notification = notificationRepository.findById(notificationId)
        .orElseThrow(() -> new BusinessException(ErrorStatus.NOTIFICATION_NOT_FOUND));
    if (!notification.getUserId().equals(userId)) {
      throw new BusinessException(ErrorStatus.FORBIDDEN_USER);
    }
    notification.markAsRead();
  }

  private User getUser(Long userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new BusinessException(ErrorStatus.USER_NOT_FOUND));
  }
}
