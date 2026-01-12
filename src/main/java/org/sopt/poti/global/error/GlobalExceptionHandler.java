package org.sopt.poti.global.error;

import lombok.extern.slf4j.Slf4j;
import org.sopt.poti.global.common.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    String errorMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
    log.error("유효성 검증 실패: {}", errorMessage);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
        ApiResponse.fail(ErrorStatus.BAD_REQUEST, errorMessage)
    );
  }

  @ExceptionHandler(NoHandlerFoundException.class)
  public ResponseEntity<ApiResponse<Void>> handleNoHandlerFoundException(
      NoHandlerFoundException e) {
    log.error("존재하지 않는 경로 요청: {}", e.getRequestURL());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
        ApiResponse.fail(ErrorStatus.NOT_FOUND_HANDLER)
    );
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ApiResponse<Void>> handleNoResourceFoundException(
      NoResourceFoundException e) {
    log.error("존재하지 않는 리소스 요청: {}", e.getResourcePath());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
        ApiResponse.fail(ErrorStatus.NOT_FOUND_HANDLER)
    );
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(
      HttpMessageNotReadableException e) {
    log.error("잘못된 요청 형식: {}", e.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
        ApiResponse.fail(ErrorStatus.BAD_REQUEST)
    );
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
    log.error("예상치 못한 예외 발생: ", e);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
        ApiResponse.fail(ErrorStatus.INTERNAL_SERVER_ERROR)
    );
  }

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
    log.error("비즈니스 예외 발생: {}", e.getErrorStatus().getMessage());
    return ResponseEntity.status(e.getErrorStatus().getHttpStatus()).body(
        ApiResponse.fail(e.getErrorStatus())
    );
  }
}
