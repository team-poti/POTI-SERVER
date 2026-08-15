package org.sopt.poti.domain.fcmtoken.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.poti.domain.fcmtoken.repository.FcmTokenRepository;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyPost;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyPostStatus;
import org.sopt.poti.domain.notification.entity.NotificationType;
import org.sopt.poti.domain.notification.service.NotificationService;
import org.sopt.poti.domain.order.entity.Order;
import org.sopt.poti.domain.user.entity.User;
import org.sopt.poti.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@Service
@RequiredArgsConstructor
@Profile("!test")
public class FcmNotificationService {

  private final FcmTokenRepository fcmTokenRepository;
  private final NotificationService notificationService;
  private final UserRepository userRepository;

  @Value("${app.deeplink.host}")
  private String deeplinkHost;

  // 새 참여자 참여 → 모집자에게
  public void notifyNewParticipant(GroupBuyPost post, String participantNickname) {
    String title = "새로운 참여자가 생겼어요 👥";
    String body = participantNickname + "님이 " + post.getTitle() + "에 참여했어요";
    String deeplink = deeplinkHost + "/recruiter-detail/" + post.getId();
    saveAndSend(post.getLeader().getId(), title, body, NotificationType.TRADE, deeplink);
  }

  // 참여자 입금 정보 제출(WAIT_PAY_CHECK) → 모집자에게
  public void notifyWaitPayCheck(GroupBuyPost post) {
    String title = "입금 확인이 필요해요 ⏳";
    String body = post.getTitle() + " 입금 확인을 기다리는 참여자가 있어요";
    String deeplink = deeplinkHost + "/participant-manage/" + post.getId();
    saveAndSend(post.getLeader().getId(), title, body, NotificationType.TRADE, deeplink);
  }

  // 모든 참여자 입금 완료(PAYMENT_DONE) → 모집자에게 배송 시작 요청
  public void notifyNeedStartDelivery(GroupBuyPost post) {
    String title = "배송 시작이 필요해요 ⏳";
    String body = post.getTitle() + " 모든 참여자의 입금이 완료되었어요. 배송을 시작해주세요!";
    String deeplink = deeplinkHost + "/participant-manage/" + post.getId();
    saveAndSend(post.getLeader().getId(), title, body, NotificationType.TRADE, deeplink);
  }

  // 분철글 상태 변경 → 모집자 + 참여자 전원 (딥링크 수신자별 상이)
  public void notifyPostStatusChanged(GroupBuyPost post, List<Order> participantOrders) {
    String emoji = resolveEmoji(post.getStatus());
    String statusMsg = resolveStatusMessage(post.getStatus());
    String title = emoji + " " + post.getTitle();
    String body = post.getTitle() + " " + statusMsg;

    String leaderDeeplink = deeplinkHost + "/recruiter-detail/" + post.getId();
    saveAndSend(post.getLeader().getId(), title, body, NotificationType.TRADE, leaderDeeplink);

    participantOrders.forEach(order -> {
      String participantDeeplink = deeplinkHost + "/participant-detail/" + order.getId();
      saveAndSend(order.getUser().getId(), title, body, NotificationType.TRADE, participantDeeplink);
    });
  }

  private void saveAndSend(Long userId, String title, String body, NotificationType type, String deeplink) {
    notificationService.save(userId, title, body, type, deeplink);

    User user = userRepository.findById(userId).orElse(null);
    if (user == null) {
      log.warn("알림 설정 조회 실패 — FCM 발송 skip: userId={}", userId);
      return;
    }
    if (!isAllowed(user, type)) {
      return;
    }

    if (TransactionSynchronizationManager.isActualTransactionActive()) {
      TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
        @Override
        public void afterCommit() {
          sendToUser(userId, title, body, deeplink);
        }
      });
    } else {
      sendToUser(userId, title, body, deeplink);
    }
  }

  private boolean isAllowed(User user, NotificationType type) {
    return switch (type) {
      case TRADE -> user.isTradeNotificationEnabled();
      case EVENT -> user.isEventNotificationEnabled();
    };
  }

  private void sendToUser(Long userId, String title, String body, String deeplink) {
    List<String> tokens = fcmTokenRepository.findAllByUser_Id(userId).stream()
        .map(fcmToken -> fcmToken.getToken())
        .toList();
    tokens.forEach(token -> send(token, title, body, deeplink));
  }

  private void send(String token, String title, String body, String deeplink) {
    try {
      Message message = Message.builder()
          .setToken(token)
          .putData("title", title)
          .putData("body", body)
          .putData("deeplink", deeplink)
          .build();
      FirebaseMessaging.getInstance().send(message);
    } catch (FirebaseMessagingException e) {
      if (e.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED) {
        fcmTokenRepository.deleteByToken(token);
      }
      log.warn("FCM 발송 실패: error={}", e.getMessage());
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
