package org.sopt.poti.global.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.poti.global.common.ApiResponse;
import org.sopt.poti.global.error.ErrorStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.error("인증 실패 (401 Unauthorized): {}", authException.getMessage());
        
        // 인증되지 않은 사용자가 보호된 리소스에 접근할 때 발생 (로그인 필요)
        // ErrorStatus.INVALID_TOKEN 등을 사용할 수도 있지만, 
        // 여기서는 가장 일반적인 "인증 실패" 의미를 전달하거나, 필요에 따라 세분화 가능
        // 우선 기존에 정의된 INVALID_TOKEN을 사용하거나 새로운 401 에러를 정의해서 사용
        
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // ApiResponse.fail()을 사용하여 통일된 에러 응답 객체 생성
        ApiResponse<?> apiResponse = ApiResponse.fail(ErrorStatus.UNAUTHORIZED_USER);

        objectMapper.writeValue(response.getWriter(), apiResponse);
    }
}
