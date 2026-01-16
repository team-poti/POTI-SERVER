package org.sopt.poti.domain.order.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.delivery.dto.request.StartDeliveryRequest;
import org.sopt.poti.domain.delivery.dto.response.StartDeliveryResponse;
import org.sopt.poti.domain.delivery.entity.Delivery;
import org.sopt.poti.domain.delivery.service.DeliveryService;
import org.sopt.poti.domain.groupbuy.dto.response.PostParticipantListResponse;
import org.sopt.poti.domain.groupbuy.dto.response.PostParticipantListResponse.DepositInfo;
import org.sopt.poti.domain.groupbuy.dto.response.PostParticipantListResponse.MemberPerPrice;
import org.sopt.poti.domain.groupbuy.dto.response.PostParticipantListResponse.PostParticipantResponse;
import org.sopt.poti.domain.groupbuy.dto.response.PostParticipantListResponse.PriceInfo;
import org.sopt.poti.domain.groupbuy.dto.response.PostParticipantListResponse.ShippingInfo;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyPost;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyPostStatus;
import org.sopt.poti.domain.order.entity.DeliveryInfo;
import org.sopt.poti.domain.order.entity.Order;
import org.sopt.poti.domain.order.entity.OrderItem;
import org.sopt.poti.domain.order.entity.OrderStatus;
import org.sopt.poti.domain.order.repository.OrderItemRepository;
import org.sopt.poti.domain.order.repository.OrderRepository;
import org.sopt.poti.domain.payment.entity.Payment;
import org.sopt.poti.global.error.BusinessException;
import org.sopt.poti.global.error.ErrorStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

  private final OrderRepository orderRepository;
  private final OrderItemRepository orderItemRepository;
  private final DeliveryService deliveryService;

  public int countByUser_Id(Long userId) {
    return orderRepository.countByUser_Id(userId);
  }

  public int countByUser_IdAndStatusIn(Long userId, List<OrderStatus> statuses) {
    return orderRepository.countByUser_IdAndStatusIn(userId, statuses);
  }

  public Order getOrderById(Long orderId) {
    return orderRepository.findById(orderId)
        .orElseThrow(() -> new BusinessException(ErrorStatus.ORDER_NOT_FOUND));
  }

  public void validateDelivered(Order order) {
    if (order.getStatus() != OrderStatus.DELIVERED) {
      throw new BusinessException(ErrorStatus.ORDER_NOT_COMPLETED);
    }
  }

  public void validateOrderOwner(Order order, Long writerUserId) {
    if (!order.getUser().getId().equals(writerUserId)) {
      throw new BusinessException(ErrorStatus.REVIEW_FORBIDDEN);
    }
  }

  public List<OrderItem> getOrderItemsByOptionIds(List<Long> optionIds) {
    return orderItemRepository.findAllByGroupBuyOptionIdIn(optionIds);
  }


  public List<Order> getOrdersByUser(Long userId) {
    return orderRepository.findByUser_IdOrderByCreatedAtDesc(userId);
  }

  // 해당 분철글에 대해 OrderStatus가 아닌 주문이 남아있는지 확인
  public long countByGroupBuyPostIdAndStatusNot(Long postId, OrderStatus status) {
    return orderRepository.countUnpaidOrders(postId, status);
  }

  // 배송사 등록
  @Transactional
  public StartDeliveryResponse startOrderDelivery(Long userId, Long orderId,
      StartDeliveryRequest startDeliveryRequest) {
    Order order = orderRepository.findByIdWithPostAndLeader(orderId)
        .orElseThrow(() -> new BusinessException(ErrorStatus.ORDER_NOT_FOUND));

    GroupBuyPost groupBuyPost = order.getGroupBuyPost();
    // 분철글이 본인이 작성한 글인지 확인
    if (!groupBuyPost.getLeader().getId().equals(userId)) {
      throw new BusinessException(ErrorStatus.FORBIDDEN_USER);
    }

    // 이미 배송 처리된 주문건
    if (deliveryService.existsDeliveryByOrderId(orderId)) {
      throw new BusinessException(ErrorStatus.ORDER_EXISTS_SHIPPINGS);
    }

    LocalDateTime shippedAt = LocalDateTime.now();

    Delivery delivery = Delivery.builder()
        .trackingNumber(startDeliveryRequest.trackingNumber())
        .carrier(startDeliveryRequest.carrier())
        .shippedAt(shippedAt)
        .order(order)
        .build();

    deliveryService.saveDelivery(delivery);
    order.startDelivery();

    return new StartDeliveryResponse(orderId, order.getStatus(),
        startDeliveryRequest.trackingNumber(), shippedAt);
  }

  // post에 참여한 사용자 조회
  public PostParticipantListResponse findParticipants(Long postId) {
    List<Order> orders = orderRepository.findAllOrdersWithBasicInfo(postId);

    // 공통 - 참여한 멤버옵션들, OrderStatus, PostParticipantResponse (DepositInfo, ShippingInfo 제외)
    // 1. 모집대기 - 공통만
    // 2. 입금 확인중 - 공통 + DepositInfo
    // 3. 입금 완료 - ShippingInfo(송장번호 null)
    // 4. 배송 시작 - ShippingInfo
    // 5. 배송 완료 - ShippingInfo(송장번호만)

    // 2. DTO 변환 (여기서 배치 로딩으로 연관 데이터 쿼리 나감)
    List<PostParticipantResponse> participantResponses = orders.stream()
        .map(order -> convertToParticipantResponse(order.getGroupBuyPost().getStatus(), order)
        )
        .toList();

    return new PostParticipantListResponse(participantResponses);
  }

  // 단일 주문을 응답 DTO로 변환하는 메인 로직
  private PostParticipantResponse convertToParticipantResponse(
      GroupBuyPostStatus groupBuyPostStatus, Order order) {
    String status = order.getStatus().name();

    if (groupBuyPostStatus.equals(GroupBuyPostStatus.RECRUITING)) {
      status = groupBuyPostStatus.name();
    }

    return new PostParticipantResponse(
        order.getId(),
        order.getUser().getId(),
        order.getUser().getProfileImageUrl(), // User는 이미 Fetch Join 됨
        order.getUser().getNickname(),
        getMemberNames(order),            // 아티스트 이름 리스트 추출
        status,
        createPriceInfo(order),           // 금액 정보 (항상 존재)
        createDepositInfo(order, status), // 입금 정보 (조건부)
        createShippingInfo(order, status) // 배송 정보 (조건부)
    );
  }

  //=== Helper Methods (조건 로직 분리) ===//

  // 1. 멤버 이름 리스트 추출
  private List<String> getMemberNames(Order order) {
    return order.getOrderItems().stream()
        .map(item -> item.getGroupBuyOption().getMember().getName())
        .toList();
  }

  // 2. 금액 정보 생성 (항상 노출)
  private PriceInfo createPriceInfo(Order order) {
    List<MemberPerPrice> memberPerPrices = order.getOrderItems().stream()
        .map(item -> new MemberPerPrice(
            item.getGroupBuyOption().getMember().getName(),
            item.getUnitPrice() // 단가
        ))
        .toList();

    return new PriceInfo(
        memberPerPrices,
        order.getDeliveryMethod().getName(), // "준등기" 등
        order.getDeliveryMethod().getPrice(),
        order.getTotalAmount()
    );
  }

  // 3. 입금 정보 생성 (입금 확인중 이상부터 노출)
  private DepositInfo createDepositInfo(Order order, String status) {
    // 분철글 모집 대기(WAIT_PAY) 상태면 안 보여줌 && 입금 대기 중 상태면 안보여줌
    if (Objects.equals(status, OrderStatus.WAIT_PAY.name()) || Objects.equals(status,
        GroupBuyPostStatus.RECRUITING.name())) {
      return null;
    }

    // 만약 Payment가 없다면 null 처리
    Payment payment = order.getPayment();
    if (payment != null) {
      return new DepositInfo(
          payment.getDepositor(),
          payment.getDepositedAt()
      );
    } else {
      return null;
    }
  }

  // 4. 배송 정보 생성 (입금 완료 이상부터 노출)
  private ShippingInfo createShippingInfo(Order order, String status) {
    // 모집 대기(WAIT_PAY) or 입금 확인중(WAIT_PAY_CHECK)이면 안 보여줌
    if (status.equals(OrderStatus.WAIT_PAY.name()) || status.equals(
        OrderStatus.WAIT_PAY_CHECK.name()) || status.equals(GroupBuyPostStatus.RECRUITING.name())) {
      return null;
    }

    // 상태: PAID(입금완료), SHIPPED, DELIVERED
    DeliveryInfo info = order.getDeliveryInfo();
    Delivery deliveryByOrderId = deliveryService.getDeliveryByOrderId(order.getId());

    if (info == null && deliveryByOrderId == null) {
      return null; // 방어 로직
    }

    // 송장번호가 null이면 null로 반환
    String trackingNumber =
        (deliveryByOrderId != null) ? deliveryByOrderId.getTrackingNumber() : null;

    return new ShippingInfo(
        Objects.requireNonNull(info).getReceiverName(),
        info.getAddressLine(),
        info.getPhone(),
        trackingNumber
    );
  }
}