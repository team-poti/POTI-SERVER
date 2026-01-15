package org.sopt.poti.domain.order.entity;

public enum OrderStatus {
  WAIT_PAY, // 입금 대기
  WAIT_PAY_CHECK, //입금 확인 대기
  PAID, //입금 완료

  //배송 관련 상태값
  READY, //배송 대기
  SHIPPED, //배송 시작
  DELIVERED //배송 완료
}