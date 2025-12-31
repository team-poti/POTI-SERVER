package org.sopt.poti.global.common;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(int code, String msg, T data) {

  public static <T> ApiResponse<T> success(SuccessStatus status, T data) {
    return new ApiResponse<>(
        status.getHttpStatus().value(),
        status.getMessage(),
        data
    );
  }

  public static <T> ApiResponse<T> success(SuccessStatus status) {
    return new ApiResponse<>(
        status.getHttpStatus().value(),
        status.getMessage(),
        null
    );
  }

  public static <T> ApiResponse<T> created(SuccessStatus status, T data) {
    return new ApiResponse<>(
        status.getHttpStatus().value(),
        status.getMessage(),
        data
    );
  }
  
}
