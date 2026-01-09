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
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (BusinessException ex) { // BusinessException만 잡도록 변경
            log.error("BusinessException 발생: {}", ex.getErrorStatus().getMessage());
            setErrorResponse(response, ex.getErrorStatus());
        } catch (Exception ex) { // 그 외 예상치 못한 모든 예외
            log.error("예상치 못한 예외 발생: {}", ex.getMessage(), ex);
            // 모든 예상치 못한 예외는 내부 서버 오류로 처리 (클라이언트에게 자세한 정보 노출 방지)
            setErrorResponse(response, ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ErrorStatus를 인자로 받도록 변경
    private void setErrorResponse(HttpServletResponse response, ErrorStatus errorStatus) throws IOException {
        response.setStatus(errorStatus.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // ApiResponse.fail()을 사용하여 통일된 에러 응답 객체 생성
        ApiResponse<?> apiResponse = ApiResponse.fail(errorStatus);

        // 객체를 JSON 문자열로 변환하여 응답에 작성
        objectMapper.writeValue(response.getWriter(), apiResponse);
    }
}
