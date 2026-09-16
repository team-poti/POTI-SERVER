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

    if (jwt != null) {
      try {
        tokenProvider.validateToken(jwt);
        Long userId = tokenProvider.getUserIdFromToken(jwt);

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
      } catch (BusinessException e) {
        // USER_SUSPENDED는 게스트 접근도 막아야 하므로 재throw
        if (e.getErrorStatus() == ErrorStatus.USER_SUSPENDED) {
          throw e;
        }
        // 만료·위변조 토큰 등 인증 실패 시 인증 없이 계속 진행
        // permitAll 엔드포인트는 게스트로 처리되고, 인증 필요 엔드포인트는 이후 필터에서 401 반환
        log.warn("JWT 인증 실패 (게스트로 처리): {}", e.getErrorStatus().getMessage());
      }
    }

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

