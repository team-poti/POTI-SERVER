package org.sopt.poti.domain.notification.dto.request;

public record NotificationSettingRequest(
    boolean tradeNotificationEnabled,
    boolean eventNotificationEnabled
) {}
