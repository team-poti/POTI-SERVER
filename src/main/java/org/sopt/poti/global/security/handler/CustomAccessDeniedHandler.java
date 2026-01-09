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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.error("권한 부족 (403 Forbidden): {}", accessDeniedException.getMessage());

        // 로그인했지만 해당 리소스에 접근 권한이 없는 경우 (예: USER가 ADMIN API 호출)
        
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // ErrorStatus에 403 관련 에러가 없으므로 추가하거나, 임시로 BAD_REQUEST 등을 사용해야 함.
        // 하지만 정확한 의미 전달을 위해 ErrorStatus에 FORBIDDEN 관련 코드를 추가하는 것이 좋음.
        // 현재는 ErrorStatus에 403이 없으므로, ApiResponse를 직접 구성하거나 ErrorStatus 추가 필요.
        // 여기서는 임시로 40000(BAD_REQUEST)을 쓰되 메시지를 변경하는 방식은 Enum이라 불가하므로,
        // ErrorStatus에 FORBIDDEN 추가를 강력 권장함.
        
        // ErrorStatus에 FORBIDDEN_USER 사용
        ApiResponse<?> apiResponse = ApiResponse.fail(ErrorStatus.FORBIDDEN_USER); 

        objectMapper.writeValue(response.getWriter(), apiResponse);
    }
}
