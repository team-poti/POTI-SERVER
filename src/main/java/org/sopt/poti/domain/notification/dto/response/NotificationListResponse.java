package org.sopt.poti.domain.notification.dto.response;

import java.util.List;
import org.springframework.data.domain.Slice;

public record NotificationListResponse(
    List<NotificationResponse> content,
    boolean hasNext
) {

  public static NotificationListResponse from(Slice<NotificationResponse> slice) {
    return new NotificationListResponse(slice.getContent(), slice.hasNext());
  }
}
