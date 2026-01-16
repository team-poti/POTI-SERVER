package org.sopt.poti.domain.participation.dto.response;

import java.util.List;

public record ParticipationDetailResponse(
    Long participationId,
    String imageUrl,
    String artistName,
    String title,
    String postStatusText, // 상품명 아래 상태 (모집대기, 모집완료 등)
    String statusMessage,  // 안내 문구
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
      String depositStatus,
      String bank,
      String accountNumber,
      String accountHolder,
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
      String shippingStatus
  ) {

  }
}