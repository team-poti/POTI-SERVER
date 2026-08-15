package org.sopt.poti.domain.notification.dto.response;

import org.sopt.poti.domain.user.entity.User;

public record NotificationSettingResponse(
    boolean tradeNotificationEnabled,
    boolean eventNotificationEnabled
) {
  public static NotificationSettingResponse from(User user) {
    return new NotificationSettingResponse(
        user.isTradeNotificationEnabled(),
        user.isEventNotificationEnabled()
    );
  }
}
