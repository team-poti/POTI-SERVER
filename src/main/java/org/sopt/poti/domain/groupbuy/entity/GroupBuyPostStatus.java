package org.sopt.poti.domain.groupbuy.entity;

//분철글 자체의 상태값
public enum GroupBuyPostStatus {
    RECRUITING, //모집중
    CLOSED, //모집 완료
    PAYMENT_DONE,//입금완료
    SHIPPING,//배송시작
    DELIVERED,//배송완료
    COMPLETED //거래 완료
}
