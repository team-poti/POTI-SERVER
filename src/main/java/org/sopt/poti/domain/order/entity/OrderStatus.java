package org.sopt.poti.domain.order.entity;

public enum OrderStatus {
  //데모데이를 위한 상태값(이후 리펙토링 필요)
  RECRUITING, //각각의 주문에 대한 모집중 상태값
  
  WAIT_PAY, // 입금 대기
  WAIT_PAY_CHECK, //입금 확인 대기
  PAID, //입금 완료

  //배송 관련 상태값
  READY, //배송 대기
  SHIPPED, //배송 시작
  DELIVERED //배송 완료
}