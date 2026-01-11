package org.sopt.poti.global.error;

import org.sopt.poti.global.common.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    // 유효성 검증 실패
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
        ApiResponse.fail(ErrorStatus.BAD_REQUEST)
    );
  }

  @ExceptionHandler(NoHandlerFoundException.class)
  public ResponseEntity<ApiResponse<Void>> handleNoHandlerFoundException(
      NoHandlerFoundException e) {
    // 없는 API URL 요청
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
        ApiResponse.fail(ErrorStatus.NOT_FOUND_HANDLER)
    );
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(
      HttpMessageNotReadableException e) {
    // JSON 파싱 오류 등
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
        ApiResponse.fail(ErrorStatus.BAD_REQUEST)
    );
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
    // 예외처리 못한 모든 Exception
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
        ApiResponse.fail(ErrorStatus.INTERNAL_SERVER_ERROR)
    );
  }

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
    // 커스텀 예외
    return ResponseEntity.status(e.getErrorStatus().getHttpStatus()).body(
        ApiResponse.fail(e.getErrorStatus())
    );
  }
}
