package org.sopt.poti.global.dev;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.delivery.repository.DeliveryRepository;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyOption;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyPost;
import org.sopt.poti.domain.groupbuy.repository.GroupBuyRepository;
import org.sopt.poti.domain.order.entity.Order;
import org.sopt.poti.domain.order.entity.OrderStatus;
import org.sopt.poti.domain.order.repository.OrderItemRepository;
import org.sopt.poti.domain.order.repository.OrderRepository;
import org.sopt.poti.domain.payment.repository.PaymentRepository;
import org.sopt.poti.domain.user.repository.UserRepository;
import org.sopt.poti.global.error.BusinessException;
import org.sopt.poti.global.error.ErrorStatus;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// ... (기존 imports)

@Service
@RequiredArgsConstructor
@Profile({"local", "dev"})
public class DevService {

  private final UserRepository userRepository;
  private final GroupBuyRepository groupBuyRepository;
  private final OrderRepository orderRepository;
  private final OrderItemRepository orderItemRepository;
  private final PaymentRepository paymentRepository; // 추가
  private final DeliveryRepository deliveryRepository; // 추가

  @Transactional
  public void hardDeleteUser(Long userId) {
    List<GroupBuyPost> myPosts = groupBuyRepository.findAllByLeaderId(userId);

    List<Long> myOptionIds = myPosts.stream()
        .flatMap(post -> post.getOptions().stream())
        .map(GroupBuyOption::getId)
        .toList();

    if (!myOptionIds.isEmpty()) {
      orderItemRepository.deleteByGroupBuyOptionIdIn(myOptionIds);
    }

    // 2. 유저가 쓴 게시글 삭제 (Cascade로 옵션, 이미지, 배송정보 삭제됨)
    groupBuyRepository.deleteByLeaderId(userId);

    // 3. 유저의 주문 내역 삭제 (Cascade로 본인의 OrderItem 삭제됨)
    orderRepository.deleteByUser_Id(userId);

    // 4. 유저 삭제
    userRepository.deleteById(userId);
  }

  // 주문 상태 강제 변경 (테스트용)
  @Transactional
  public void resetOrderStatus(Long orderId, OrderStatus targetStatus) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new BusinessException(ErrorStatus.ORDER_NOT_FOUND));

    // 목표 상태에 따라 연관 데이터 정리
    if (targetStatus == OrderStatus.WAIT_PAY) {
        // WAIT_PAY로 초기화 시, 입금 정보와 운송장 정보 모두 삭제
        paymentRepository.deleteByOrder_Id(orderId);
        deliveryRepository.deleteByOrder_Id(orderId);
    } else if (targetStatus == OrderStatus.WAIT_PAY_CHECK || targetStatus == OrderStatus.PAID) {
        // WAIT_PAY_CHECK 또는 PAID로 초기화 시, 운송장 정보만 삭제 (입금 정보는 유지)
        deliveryRepository.deleteByOrder_Id(orderId);
    }
    // SHIPPED, DELIVERED 등 그 이상 상태로 변경 시에는 삭제할 것이 없음

    // 주문 상태 변경 적용
    order.updateStatus(targetStatus);
  }
}
