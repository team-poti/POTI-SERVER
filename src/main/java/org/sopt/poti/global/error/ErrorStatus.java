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
  BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
  PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),

  /**
   * 401 Unauthorized
   */
  INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),

  /**
   * 404 Not Found
   */
  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
  ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 상품입니다."),

  /**
   * 500 Internal Server Error
   */
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다.");

  private final HttpStatus httpStatus;
  private final String message;
}
