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
  INVALID_SOCIAL_TYPE(40001, HttpStatus.BAD_REQUEST, "지원하지 않는 소셜 로그인 타입입니다."),

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

  /**
   * 404 Not Found
   */
  USER_NOT_FOUND(40400, HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
  ITEM_NOT_FOUND(40401, HttpStatus.NOT_FOUND, "존재하지 않는 상품입니다."),
  NOT_FOUND_HANDLER(40402, HttpStatus.NOT_FOUND, "존재하지 않는 API 경로입니다."),
  ARTIST_NOT_FOUND(40403, HttpStatus.NOT_FOUND, "존재하지 않는 아티스트입니다."),
  MEMBER_NOT_FOUND(40404, HttpStatus.NOT_FOUND, "존재하지 않는 멤버입니다."),
  DELIVERY_METHOD_NOT_FOUND(40405, HttpStatus.NOT_FOUND, "존재하지 않는 배송 방법입니다."),

  /**
   * 500 Internal Server Error
   */
  INTERNAL_SERVER_ERROR(50000, HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다."),
  EXTERNAL_API_ERROR(50001, HttpStatus.INTERNAL_SERVER_ERROR, "외부 API 호출 중 오류가 발생했습니다.");

  private final int code;
  private final HttpStatus httpStatus;
  private final String message;
}
