package org.sopt.poti.domain.groupbuy.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

//분철글 자체의 상태값
@Getter
@RequiredArgsConstructor
public enum GroupBuyPostStatus {
  RECRUITING(
      "참여자들을 기다리고 있어요",
      "다른 참여자들을 기다리고 있어요"
  ), //모집중
  CLOSED(
      "입금을 기다리는 중이에요",
      "지금 입금해주세요!"
  ), //모집 완료
  PAYMENT_DONE(
      "배송을 기다리는 참여자가 있어요",
      "모집자가 배송을 준비 중이에요"
  ),//입금완료
  SHIPPING(
      "배송을 시작했어요",
      "모집자가 배송을 시작했어요"
  ),//배송시작
  DELIVERED(
      "거래가 종료되었어요",
      ""
  );//배송완료

  private final String sellerMessage; // 판매자용
  private final String buyerMessage;  // 참여자용
}
