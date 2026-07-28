package org.sopt.poti.global.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.poti.domain.user.entity.User;
import org.sopt.poti.domain.user.entity.UserStatus;
import org.sopt.poti.domain.user.repository.UserRepository;
import org.sopt.poti.global.error.BusinessException;
import org.sopt.poti.global.error.ErrorStatus;
import org.sopt.poti.global.security.UserPrincipal;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider tokenProvider;
  private final UserRepository userRepository; // User 엔티티 조회용

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException {
    String jwt = getJwtFromRequest(request);

    if (jwt != null) { // JWT가 존재할 경우에만 유효성 검사 및 인증 처리
      tokenProvider.validateToken(jwt); // 여기서 예외 발생 시 JwtExceptionFilter가 잡음
      Long userId = tokenProvider.getUserIdFromToken(jwt);

      // User 엔티티 조회 (UserPrincipal 생성을 위해 필요)
      User user = userRepository.findById(userId)
          .orElseThrow(() -> new BusinessException(ErrorStatus.USER_NOT_FOUND));

      if (user.getStatus() == UserStatus.SUSPENDED) {
        throw new BusinessException(ErrorStatus.USER_SUSPENDED);
      }

      UserPrincipal userPrincipal = UserPrincipal.create(user);
      UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
          userPrincipal, null, userPrincipal.getAuthorities());
      authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

      SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    // JWT가 없거나 유효하지 않아도 필터 체인은 계속 진행.
    // SecurityConfig에서 .anyRequest().authenticated()로 설정했으므로,
    // 토큰이 없거나 유효하지 않아 인증 객체가 SecurityContext에 없으면
    // 다음 필터에서 Unauthorized (401) 처리됨.

    filterChain.doFilter(request, response);
  }

  private String getJwtFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }
}

