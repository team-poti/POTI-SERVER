package org.sopt.poti.domain.groupbuy.dto.response;

import java.util.List;

public record PostParticipantListResponse(
    List<PostParticipantResponse> participants
) {

  public record PostParticipantResponse(
      Long orderId,
      Long userId,
      String profileImage,
      String nickname,
      List<String> memberNames, // 구매한 아티스트 멤버들 (클라이언트에서 유진, 레이 로 이어서 사용)
      String status, // 해당 주문 상태
      PriceInfo priceInfo,  // 금액 정보
      DepositInfo depositInfo,  // 입금 정보
      ShippingInfo shippingInfo // 배송 정보
  ) {

  }

  // 입금해야할 금액 정보
  public record PriceInfo(
      List<MemberPerPrice> memberPerPrices,
      String shippingName,
      Integer shippingPrice,
      Integer totalPrice
  ) {

  }

  public record MemberPerPrice(
      String name,  // 아티스트 멤버 이름
      Integer price // 아티스트 멤버 굿즈 가격
  ) {

  }

  // 입금정보
  public record DepositInfo(
      String depositorName,
      String depositTime
  ) {

  }

  // 배송 정보
  public record ShippingInfo(
      String receiverName,  // 받는 사람 정보
      String address,
      String phone, // 휴대폰 번호
      String trackingNumber // 송장 번호
  ) {

  }
}
