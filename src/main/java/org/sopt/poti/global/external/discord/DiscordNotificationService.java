package org.sopt.poti.global.external.discord;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscordNotificationService {

  @Value("${discord.webhook-url}")
  private String webhookUrl;

  private final ObjectMapper objectMapper;
  private final RestTemplate restTemplate = new RestTemplate();

  @Async
  public void sendErrorNotification(Exception e, HttpServletRequest request) {
    try {
      // 메시지 구성 (JSON)
      Map<String, String> body = new HashMap<>();
      String content = String.format("🚨 **500 Server Error**\n" +
              "**URI:** %s %s\n" +
              "**Message:** %s\n" +
              "**Client IP:** %s",
          request.getMethod(),
          request.getRequestURI(),
          e.getMessage(),
          request.getRemoteAddr());

      body.put("content", content);

      // 헤더 설정
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);

      // 요청 엔티티 생성
      HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(body), headers);

      // Webhook 전송
      restTemplate.postForEntity(webhookUrl, entity, String.class);

    } catch (JsonProcessingException jsonException) {
      log.error("디스코드 알림 JSON 변환 실패", jsonException);
    } catch (Exception ex) {
      log.error("디스코드 알림 전송 실패", ex);
    }
  }
}
