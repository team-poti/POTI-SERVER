package org.sopt.poti.global.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.poti.global.error.BusinessException;
import org.sopt.poti.global.error.ErrorStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

  @Value("${jwt.secret}")
  private String secretKey;

  @Value("${jwt.access-token-validity}")
  private long accessTokenValidity;

  @Value("${jwt.refresh-token-validity}")
  private long refreshTokenValidity;

  private Key key;

  @PostConstruct
  protected void init() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    this.key = Keys.hmacShaKeyFor(keyBytes);
  }

  public String createAccessToken(Long userId) {
    return createToken(userId, accessTokenValidity);
  }

  public String createRefreshToken(Long userId) {
    return createToken(userId, refreshTokenValidity);
  }

  private String createToken(Long userId, long validity) {
    Claims claims = Jwts.claims().subject(String.valueOf(userId)).build();
    Date now = new Date();
    Date validityDate = new Date(now.getTime() + validity);

    return Jwts.builder().claims(claims)
        .issuedAt(now)
        .expiration(validityDate)
        .signWith((javax.crypto.SecretKey) key)
        .compact();
  }

  public Long getUserIdFromToken(String token) {
    return Long.parseLong(getClaimsFromToken(token).getSubject());
  }

  public Claims getClaimsFromToken(String token) {
    try {
      return Jwts.parser()
          .verifyWith((javax.crypto.SecretKey) key)
          .build()
          .parseSignedClaims(token)
          .getPayload();
    } catch (SecurityException | MalformedJwtException e) {
      log.error("잘못된 JWT 서명입니다: {}", e.getMessage());
      throw new BusinessException(ErrorStatus.SIGNATURE_INVALID_JWT_TOKEN);
    } catch (ExpiredJwtException e) {
      log.error("만료된 JWT 토큰입니다: {}", e.getMessage());
      throw new BusinessException(ErrorStatus.EXPIRED_JWT_TOKEN);
    } catch (UnsupportedJwtException e) {
      log.error("지원되지 않는 JWT 토큰입니다: {}", e.getMessage());
      throw new BusinessException(ErrorStatus.UNSUPPORTED_JWT_TOKEN);
    } catch (IllegalArgumentException e) {
      log.error("JWT 토큰이 잘못되었습니다 (비어있거나 null 등): {}", e.getMessage());
      throw new BusinessException(ErrorStatus.MISSING_JWT_TOKEN);
    }
  }

  public void validateToken(String token) {
    getClaimsFromToken(token); // 예외가 발생하면 throw 됨
  }
}
