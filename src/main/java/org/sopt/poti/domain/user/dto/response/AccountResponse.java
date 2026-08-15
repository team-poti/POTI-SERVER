package org.sopt.poti.domain.user.dto.response;

import org.sopt.poti.domain.user.entity.SocialType;
import org.sopt.poti.domain.user.entity.User;

public record AccountResponse(
    String nickname,
    String email,
    SocialType socialType
) {
  public static AccountResponse from(User user) {
    return new AccountResponse(user.getNickname(), user.getEmail(), user.getSocialType());
  }
}
