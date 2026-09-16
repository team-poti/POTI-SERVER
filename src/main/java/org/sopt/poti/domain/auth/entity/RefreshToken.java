package org.sopt.poti.domain.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@RedisHash(value = "refreshToken")
@AllArgsConstructor
@Getter
@Builder
public class RefreshToken {

  @Id
  private Long userId; // Redis Key => refreshToken:{userId}

  private String refreshToken;

  @TimeToLive
  private Long ttl; // 만료 시간 (초 단위)

  public void updateRefreshToken(String refreshToken, Long ttl) {
    this.refreshToken = refreshToken;
    this.ttl = ttl;
  }
}
