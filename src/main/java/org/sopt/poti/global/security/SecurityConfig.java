package org.sopt.poti.global.security;

import lombok.RequiredArgsConstructor;
import org.sopt.poti.global.security.handler.CustomAccessDeniedHandler;
import org.sopt.poti.global.security.handler.CustomAuthenticationEntryPoint;
import org.sopt.poti.global.security.jwt.JwtAuthenticationFilter;
import org.sopt.poti.global.security.jwt.JwtExceptionFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final JwtExceptionFilter jwtExceptionFilter;
  private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
  private final CustomAccessDeniedHandler customAccessDeniedHandler;

  // 인증 없이 허용할 API 엔드포인트 목록
  private static final String[] WHITE_LIST = {
      "/api/v1/auth/**", // 로그인/회원가입 등 인증 관련
      "/actuator/health", // 헬스 체크
      "/error",
      "/swagger-ui/**", // Swagger 문서 관련
      "/v3/api-docs/**",
      "/favicon.ico",
      "/css/**",
      "/dev/login", // 개발자용 자동 로그인 (토큰 발급)
  };

  // 게스트 허용 (ADR-011)
  private static final String[] GUEST_LIST = {
      "/api/v1/home",
      "/api/v1/feeds",
      "/api/v1/search/**",
      "/api/v1/artists/**",
      "/api/v1/shippings",
  };

  @Bean
  @Order(2)
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable) // CSRF 보호 비활성화 (Stateless)
        .formLogin(AbstractHttpConfigurer::disable) // 폼 로그인 비활성화
        .httpBasic(AbstractHttpConfigurer::disable) // HTTP Basic 인증 비활성화
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 사용 안 함

        // URL별 권한 설정
        // {postId} 패턴이 /posts/me 등 seller 경로를 삼키지 않도록 authenticated() 규칙을 먼저 선언
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(WHITE_LIST).permitAll()
            .requestMatchers("/api/v1/posts/me").authenticated()
            .requestMatchers("/api/v1/posts/{postId}/participants").authenticated()
            .requestMatchers("/api/v1/posts/sale/{postId}").authenticated()
            .requestMatchers(GUEST_LIST).permitAll()
            .requestMatchers("/api/v1/posts/{postId}").permitAll()
            .anyRequest().authenticated()
        )

        // 예외 처리 핸들러 등록
        .exceptionHandling(exception -> exception
            .authenticationEntryPoint(customAuthenticationEntryPoint)
            .accessDeniedHandler(customAccessDeniedHandler)
        )

        // JWT 필터 추가 (UsernamePasswordAuthenticationFilter 앞에 실행)
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        // JWT 예외 처리 필터 추가 (JwtAuthenticationFilter 앞에 실행)
        .addFilterBefore(jwtExceptionFilter, JwtAuthenticationFilter.class);

    return http.build();
  }
}
