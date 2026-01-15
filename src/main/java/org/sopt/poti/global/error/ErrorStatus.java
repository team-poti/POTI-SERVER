package org.sopt.poti.global.error;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorStatus {

  /**
   * 400 Bad Request
   */
  BAD_REQUEST(40000, HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
  INVALID_NICKNAME(40001, HttpStatus.BAD_REQUEST, "닉네임 형식이 올바르지 않습니다."), // 특수문자랑 길이 검사
  PROFANITY_DETECTED(40002, HttpStatus.BAD_REQUEST, "비속어가 포함되어 있습니다."),
  NICKNAME_DUPLICATED(40003, HttpStatus.BAD_REQUEST, "이미 사용 중인 닉네임입니다."),
  INVALID_SOCIAL_TYPE(40004, HttpStatus.BAD_REQUEST, "지원하지 않는 소셜 로그인 타입입니다."),
  ORDER_NOT_COMPLETED(40005, HttpStatus.BAD_REQUEST, "거래 완료(배송 완료) 후에만 리뷰를 작성할 수 있습니다."),
  PAYMENT_NOT_PENDING(40006, HttpStatus.BAD_REQUEST, "입금 대기 상태에서만 입금 정보를 제출할 수 있습니다."),
  PAYMENT_NOT_REQUESTED(40007, HttpStatus.BAD_REQUEST, "입금 확인 대기 상태에서만 입금 확인이 가능합니다."),
  ORDER_NOT_WAIT_PAY(40008, HttpStatus.BAD_REQUEST, "입금 대기 상태에서만 입금 정보를 제출할 수 있습니다."),
  ORDER_NOT_WAIT_PAY_CHECK(40009, HttpStatus.BAD_REQUEST, "입금 확인 대기 상태에서만 입금 완료 처리가 가능합니다."),
  ORDER_ITEM_EMPTY(40010, HttpStatus.BAD_REQUEST, "주문 항목이 비어 있습니다."),
  ORDER_ITEM_INVALID_COUNT(40011, HttpStatus.BAD_REQUEST, "주문 수량은 1 이상이어야 합니다."),
  GROUP_BUY_OPTION_NOT_IN_POST(40012, HttpStatus.BAD_REQUEST, "해당 분철글에 속하지 않은 옵션이 포함되어 있습니다."),
  DUPLICATE_ORDER_OPTION(40013, HttpStatus.BAD_REQUEST, "중복된 옵션은 한 주문에서 선택할 수 없습니다."),
  ORDER_NOT_PAID_OR_READY(40014, HttpStatus.BAD_REQUEST, "입금 완료 상태 또는 배송 대기 상태에서만 배송 중 처리가 가능합니다."),
  ORDER_EXISTS_SHIPPINGS(40015, HttpStatus.BAD_REQUEST, "이미 배송처리된 주문입니다."),

  /**
   * 401 Unauthorized
   */
  INVALID_TOKEN(40100, HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
  EXPIRED_JWT_TOKEN(40101, HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
  UNSUPPORTED_JWT_TOKEN(40102, HttpStatus.UNAUTHORIZED, "지원되지 않는 토큰 형식입니다."),
  MALFORMED_JWT_TOKEN(40103, HttpStatus.UNAUTHORIZED, "손상된 토큰입니다."),
  SIGNATURE_INVALID_JWT_TOKEN(40104, HttpStatus.UNAUTHORIZED, "유효하지 않은 JWT 서명입니다."),
  MISSING_JWT_TOKEN(40105, HttpStatus.UNAUTHORIZED, "토큰이 누락되었습니다."),
  UNAUTHORIZED_USER(40106, HttpStatus.UNAUTHORIZED, "로그인이 필요한 요청입니다."),

  /**
   * 403 Forbidden
   */
  FORBIDDEN_USER(40300, HttpStatus.FORBIDDEN, "권한이 없는 요청입니다."),
  REVIEW_FORBIDDEN(40301, HttpStatus.FORBIDDEN, "리뷰 작성 권한이 없습니다."),

  /**
   * 404 Not Found
   */
  USER_NOT_FOUND(40400, HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
  ITEM_NOT_FOUND(40401, HttpStatus.NOT_FOUND, "존재하지 않는 상품입니다."),
  ARTIST_NOT_FOUND(40402, HttpStatus.NOT_FOUND, "존재하지 않는 아티스트입니다."),
  NOT_FOUND_HANDLER(40403, HttpStatus.NOT_FOUND, "존재하지 않는 API 경로입니다."),
  MEMBER_NOT_FOUND(40404, HttpStatus.NOT_FOUND, "존재하지 않는 멤버입니다."),
  DELIVERY_METHOD_NOT_FOUND(40405, HttpStatus.NOT_FOUND, "존재하지 않는 배송 방법입니다."),
  ORDER_NOT_FOUND(40406, HttpStatus.NOT_FOUND, "존재하지 않는 주문입니다."),
  POST_NOT_FOUND(40407, HttpStatus.NOT_FOUND, "존재하지 않는 분철글입니다."),
  PAYMENT_NOT_FOUND(40408, HttpStatus.NOT_FOUND, "존재하지 않는 결제 정보입니다."),
  GROUP_BUY_SHIPPING_NOT_FOUND(40409, HttpStatus.NOT_FOUND, "해당 분철글에서 선택한 배송 옵션을 찾을 수 없습니다."),
  GROUP_BUY_OPTION_NOT_FOUND(40410, HttpStatus.NOT_FOUND, "존재하지 않는 분철 옵션이 포함되어 있습니다."),

  /**
   * 409 Conflict
   */
  REVIEW_ALREADY_EXISTS(40900, HttpStatus.CONFLICT, "이미 리뷰가 작성된 주문입니다."),
  GROUP_BUY_POST_NOT_RECRUITING(40901, HttpStatus.BAD_REQUEST, "모집중인 공구만 주문할 수 있습니다."),

  /**
   * 500 Internal Server Error
   */
  INTERNAL_SERVER_ERROR(50000, HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다."),
  EXTERNAL_API_ERROR(50001, HttpStatus.INTERNAL_SERVER_ERROR, "외부 API 호출 중 오류가 발생했습니다.");

  private final int code;
  private final HttpStatus httpStatus;
  private final String message;
}
