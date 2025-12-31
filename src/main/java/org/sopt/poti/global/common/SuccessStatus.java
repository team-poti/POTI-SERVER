package org.sopt.poti.global.common;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum SuccessStatus {

  /**
   * 200 OK
   */
  // 공통
  OK(HttpStatus.OK, "요청이 성공했습니다."),

  /**
   * 201 CREATED
   */
  // 공통
  CREATED(HttpStatus.CREATED, "생성이 완료되었습니다.");

  private final HttpStatus httpStatus;
  private final String message;
}
