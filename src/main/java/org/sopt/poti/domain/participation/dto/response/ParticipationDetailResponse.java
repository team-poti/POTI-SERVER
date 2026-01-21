package org.sopt.poti.domain.participation.dto.response;

import java.util.List;

public record ParticipationDetailResponse(
    Long participationId,
    Long postId,  // 분철글 식별자
    String orderNumber,//고유 주문번호
    String imageUrl,
    String artistName,
    String title,

    String status,//OrderStatus 기반 상단 Status (GroupBuyPostStatus같이)
    String statusMessage, // 상단 진행 멘트

    List<MemberPaymentDto> memberPayments,
    PaymentInfo paymentInfo,
    ShippingInfo shippingInfo
) {

  public record MemberPaymentDto(
      String memberName,
      int price
  ) {

  }

  public record PaymentInfo(
      int shippingFee,
      int totalAmount,
      String depositStatus, // OrderStatus
      String bank,
      String accountNumber,
      String depositDeadline
  ) {

  }

  public record ShippingInfo(
      String shippingMethod,
      String receiver,
      String zipcode,
      String address,
      String phone,
      String carrier,
      String trackingNumber,
      String shippingStatus // OrderStatus
  ) {

  }
}