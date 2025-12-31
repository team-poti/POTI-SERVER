package org.sopt.poti.global.common;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
enum SuccessStatus {

  /**
   * 200 OK
   */
  // 공통
  OK(HttpStatus.OK, "요청이 성공했습니다."),

  // Auth & User
  LOGIN_SUCCESS(HttpStatus.OK, "로그인에 성공했습니다."),
  NICKNAME_AVAILABLE(HttpStatus.OK, "사용 가능한 닉네임입니다."),
  LOGOUT_SUCCESS(HttpStatus.OK, "로그아웃이 완료되었습니다."),

  // Item (조회/수정/삭제)
  ITEM_DETAIL_SUCCESS(HttpStatus.OK, "분철 상세 조회에 성공했습니다."),
  ITEM_DELETE_SUCCESS(HttpStatus.OK, "분철글 삭제가 완료되었습니다."),

  // Order
  ORDER_HISTORY_SUCCESS(HttpStatus.OK, "분철 내역 조회에 성공했습니다."),

  /**
   * 201 CREATED
   */
  // 공통
  CREATED(HttpStatus.CREATED, "생성이 완료되었습니다."),

  // Auth & User
  SIGNUP_SUCCESS(HttpStatus.CREATED, "회원가입이 완료되었습니다."),

  // Item & Order
  ITEM_CREATED(HttpStatus.CREATED, "분철 모집글 등록이 완료되었습니다."),
  ORDER_SUCCESS(HttpStatus.CREATED, "분철 참여(주문)가 완료되었습니다."),
  REVIEW_CREATED(HttpStatus.CREATED, "후기 작성이 완료되었습니다.");


  private final HttpStatus httpStatus;
  private final String message;
}
