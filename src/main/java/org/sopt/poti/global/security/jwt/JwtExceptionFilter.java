package org.sopt.poti.global.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.poti.global.common.ApiResponse;
import org.sopt.poti.global.error.BusinessException;
import org.sopt.poti.global.error.ErrorStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtExceptionFilter extends OncePerRequestFilter {

  private final ObjectMapper objectMapper;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException {
    try {
      filterChain.doFilter(request, response);
    } catch (Exception ex) {
      // 예외가 BusinessException인지, 아니면 ServletException 등에 감싸져 있는지 확인
      BusinessException businessException = null;

      if (ex instanceof BusinessException) {
        businessException = (BusinessException) ex;
      } else if (ex.getCause() instanceof BusinessException) {
        businessException = (BusinessException) ex.getCause();
      } else if (ex instanceof ServletException && ex.getCause() instanceof BusinessException) {
        businessException = (BusinessException) ex.getCause();
      }

      if (businessException != null) {
        log.error("BusinessException 발생 (필터): {}", businessException.getErrorStatus().getMessage());
        setErrorResponse(response, businessException.getErrorStatus());
      } else {
        // 그 외 예상치 못한 모든 예외는 INTERNAL_SERVER_ERROR로 처리
        log.error("예상치 못한 예외 발생 (필터): {}", ex.getMessage(), ex);
        setErrorResponse(response, ErrorStatus.INTERNAL_SERVER_ERROR);
      }
    }
  }

  private void setErrorResponse(HttpServletResponse response, ErrorStatus errorStatus)
      throws IOException {
    response.setStatus(errorStatus.getHttpStatus().value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");

    ApiResponse<?> apiResponse = ApiResponse.fail(errorStatus);

    objectMapper.writeValue(response.getWriter(), apiResponse);
  }
}