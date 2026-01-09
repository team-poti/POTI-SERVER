package org.sopt.poti.domain.payment.entity;

public enum PaymentStatus {
    PENDING, //입금 대기
    REQUESTED, //입금 확인 대기
    CONFIRMED //입금 확인 완료
}
