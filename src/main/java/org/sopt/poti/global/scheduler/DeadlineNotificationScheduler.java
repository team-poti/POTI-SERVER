package org.sopt.poti.global.scheduler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.poti.domain.fcmtoken.service.FcmNotificationService;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyPost;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyPostStatus;
import org.sopt.poti.domain.groupbuy.repository.GroupBuyRepository;
import org.sopt.poti.domain.order.entity.Order;
import org.sopt.poti.domain.order.entity.OrderStatus;
import org.sopt.poti.domain.order.repository.OrderRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("!test")
public class DeadlineNotificationScheduler {

  private final GroupBuyRepository groupBuyRepository;
  private final OrderRepository orderRepository;
  private final FcmNotificationService fcmNotificationService;

  // 매일 오전 10시 — 모집 마감 1일 전 모집자 알림
  @Scheduled(cron = "0 0 10 * * *")
  public void notifyRecruitDeadlineTomorrow() {
    LocalDate tomorrow = LocalDate.now().plusDays(1);
    List<GroupBuyPost> posts = groupBuyRepository.findByStatusAndRecruitDeadline(
        GroupBuyPostStatus.RECRUITING, tomorrow);

    for (GroupBuyPost post : posts) {
      try {
        fcmNotificationService.notifyRecruitDeadlineTomorrow(post);
      } catch (Exception e) {
        log.warn("모집 마감 1일 전 알림 실패: postId={}", post.getId(), e);
      }
    }
  }

  // 5분마다 — 입금 마감 3시간 전, 30분 전 알림
  @Scheduled(cron = "0 0/5 * * * *")
  public void notifyPaymentDeadline() {
    LocalDateTime now = LocalDateTime.now();
    notifyPaymentReminder(now.minusHours(21), "3시간");
    notifyPaymentReminder(now.minusMinutes(1410), "30분");
  }

  private void notifyPaymentReminder(LocalDateTime windowEnd, String remainingTime) {
    LocalDateTime windowStart = windowEnd.minusMinutes(5);
    List<GroupBuyPost> posts = groupBuyRepository.findByStatusAndClosedAtBetween(
        GroupBuyPostStatus.CLOSED, windowStart, windowEnd);

    for (GroupBuyPost post : posts) {
      List<Order> orders = orderRepository.findByGroupBuyPostIdAndStatus(
          post.getId(), OrderStatus.WAIT_PAY);
      for (Order order : orders) {
        try {
          fcmNotificationService.notifyPaymentDeadlineReminder(order, remainingTime);
        } catch (Exception e) {
          log.warn("입금 마감 알림 실패: orderId={}, remainingTime={}", order.getId(), remainingTime, e);
        }
      }
    }
  }
}
