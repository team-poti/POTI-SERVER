package org.sopt.poti.domain.fcmtoken.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.poti.domain.fcmtoken.repository.FcmTokenRepository;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyPost;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyPostStatus;
import org.sopt.poti.domain.order.entity.Order;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Profile("!test")
public class FcmNotificationService {

  private final FcmTokenRepository fcmTokenRepository;

  // 새 참여자 참여 → 모집자에게
  public void notifyNewParticipant(GroupBuyPost post, String participantNickname) {
    String title = "새로운 참여자가 생겼어요 👥";
    String body = participantNickname + "님이 " + post.getTitle() + "에 참여했어요";
    sendToUser(post.getLeader().getId(), title, body);
  }

  // 참여자 입금 정보 제출(WAIT_PAY_CHECK) → 모집자에게
  public void notifyWaitPayCheck(GroupBuyPost post) {
    String title = "입금 확인이 필요해요 ⏳";
    String body = post.getTitle() + " 입금 확인을 기다리는 참여자가 있어요";
    sendToUser(post.getLeader().getId(), title, body);
  }

  // 분철글 상태 변경 → 모집자 + 참여자 전원
  public void notifyPostStatusChanged(GroupBuyPost post, List<Long> participantUserIds) {
    String emoji = resolveEmoji(post.getStatus());
    String statusMsg = resolveStatusMessage(post.getStatus());
    String title = emoji + " " + post.getTitle();
    String body = post.getTitle() + " " + statusMsg;

    sendToUser(post.getLeader().getId(), title, body);
    participantUserIds.forEach(userId -> sendToUser(userId, title, body));
  }

  // 거래 완료 → 참여자에게 후기 유도
  public void notifyDeliveryComplete(Order order) {
    String title = "후기를 남겨주세요 ⭐️";
    String body = order.getGroupBuyPost().getTitle() + " 거래는 어떠셨나요? 상대방에게 후기를 남겨주세요";
    sendToUser(order.getUser().getId(), title, body);
  }

  private void sendToUser(Long userId, String title, String body) {
    List<String> tokens = fcmTokenRepository.findAllByUser_Id(userId).stream()
        .map(fcmToken -> fcmToken.getToken())
        .toList();
    tokens.forEach(token -> send(token, title, body));
  }

  private void send(String token, String title, String body) {
    try {
      Message message = Message.builder()
          .setToken(token)
          .putData("title", title)
          .putData("body", body)
          .putData("deeplink", "") // TODO: iOS/Android 딥링크 형식 확정 후 채울 것
          .build();
      FirebaseMessaging.getInstance().send(message);
    } catch (FirebaseMessagingException e) {
      log.warn("FCM 발송 실패: token={}, error={}", token, e.getMessage());
    }
  }

  private String resolveEmoji(GroupBuyPostStatus status) {
    return switch (status) {
      case CLOSED -> "📋";
      case PAYMENT_DONE -> "💵";
      case SHIPPING -> "🚚";
      case DELIVERED -> "📦";
      default -> "📢";
    };
  }

  private String resolveStatusMessage(GroupBuyPostStatus status) {
    return switch (status) {
      case CLOSED -> "모집이 완료되었어요";
      case PAYMENT_DONE -> "입금이 완료되었어요";
      case SHIPPING -> "배송이 시작되었어요";
      case DELIVERED -> "배송이 완료되었어요";
      default -> "상태가 변경되었어요";
    };
  }
}
