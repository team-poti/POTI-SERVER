package org.sopt.poti.domain.participation.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.delivery.entity.Delivery;
import org.sopt.poti.domain.delivery.service.DeliveryService;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyPost;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyPostStatus;
import org.sopt.poti.domain.order.entity.Order;
import org.sopt.poti.domain.order.entity.OrderStatus;
import org.sopt.poti.domain.order.service.OrderService;
import org.sopt.poti.domain.participation.dto.response.ParticipationDetailResponse;
import org.sopt.poti.global.error.BusinessException;
import org.sopt.poti.global.error.ErrorStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParticipationDetailService {

  private final OrderService orderService;
  private final DeliveryService deliveryService;

  // 입금 마감 기한 포멧
  private static final DateTimeFormatter DEADLINE_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

  public ParticipationDetailResponse getParticipationDetail(Long userId, Long participationId) {
    Order order = orderService.findWithDetailsById(participationId)
        .orElseThrow(() -> new BusinessException(ErrorStatus.ORDER_NOT_FOUND));

    if (!order.getUser().getId().equals(userId)) {
      throw new BusinessException(ErrorStatus.FORBIDDEN_USER);
    }

    GroupBuyPost post = order.getGroupBuyPost();
    OrderStatus orderStatus = order.getStatus();
    GroupBuyPostStatus postStatus = post.getStatus();

    String postStatusValue = (postStatus != null) ? postStatus.name() : null;
    String orderStatusValue = (orderStatus != null) ? orderStatus.name() : null;

    // 상단 진행 멘트
    String statusMessage = determineStatusMessage(postStatus, orderStatus);

    // 1. 계좌 및 입금기한 노출 제어
    boolean showPaymentDetails =
        (postStatus != GroupBuyPostStatus.RECRUITING
            && (orderStatus == OrderStatus.WAIT_PAY || orderStatus == OrderStatus.WAIT_PAY_CHECK));

    String bank = showPaymentDetails ? post.getBankName() : null;
    String accountNumber = showPaymentDetails ? post.getAccountNumber() : null;

    // 입금 마감 기한
    String depositDeadline = null;
    if (showPaymentDetails && post.getRecruitDeadline() != null) {
      LocalDateTime deadlineAt = post.getRecruitDeadline().atTime(LocalTime.of(23, 59));
      depositDeadline = deadlineAt.format(DEADLINE_FORMATTER) + " 까지";
    }

    // 2. 배송 정보 조회 (배송/송장정보)
    Delivery delivery = deliveryService.findTopByOrder_IdOrderByIdDesc(order.getId())
        .orElse(null);

    // 배송 상태에 따른 정보값
    boolean isShippingActive = (orderStatus == OrderStatus.SHIPPED);

    String carrier = isShippingActive
        ? (delivery != null ? delivery.getCarrier() : null)
        : null;

    String trackingNumber = isShippingActive
        ? (delivery != null ? delivery.getTrackingNumber() : null)
        : null;

    return new ParticipationDetailResponse(
        order.getId(),
        order.getOrderNumber(),
        post.getRepresentativeImageUrl(),
        post.getArtist().getName(),
        post.getTitle(),
        postStatusValue,
        orderStatusValue,
        statusMessage,

        getMemberPaymentList(order),

        new ParticipationDetailResponse.PaymentInfo(
            order.getDeliveryMethod().getPrice(),
            order.getTotalAmount(),
            orderStatusValue,
            bank,
            accountNumber,
            depositDeadline
        ),

        new ParticipationDetailResponse.ShippingInfo(
            order.getDeliveryMethod().getName(),
            order.getDeliveryInfo().getReceiverName(),
            order.getDeliveryInfo().getZipcode(),
            order.getDeliveryInfo().getAddressLine(),
            order.getDeliveryInfo().getPhone(),
            carrier,
            trackingNumber,
            orderStatusValue
        )
    );
  }

  private String determineStatusMessage(GroupBuyPostStatus postStatus, OrderStatus orderStatus) {
    if (postStatus == GroupBuyPostStatus.RECRUITING) {
      return "다른 참여자들을 기다리고 있어요";
    }
    return switch (orderStatus) {
      case WAIT_PAY -> "지금 입금해주세요";
      case WAIT_PAY_CHECK -> "모집자가 입금 내역을 확인하고 있어요";
      case PAID, READY -> "모집자가 배송을 준비 중이에요";
      case SHIPPED -> "모집자가 배송을 시작했어요";
      case DELIVERED -> "거래가 종료되었어요";
      default -> null;
    };
  }

  private List<ParticipationDetailResponse.MemberPaymentDto> getMemberPaymentList(Order order) {
    return order.getOrderItems().stream()
        .map(item -> new ParticipationDetailResponse.MemberPaymentDto(
            item.getGroupBuyOption().getMember().getName(),
            item.getItemAmount()
        ))
        .toList();
  }
}
