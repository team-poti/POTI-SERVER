package org.sopt.poti.global.external.kakao.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoUserResponse {

  private Long id;

  @JsonProperty("kakao_account")
  private KakaoAccount kakaoAccount;

  @Getter
  @NoArgsConstructor
  public static class KakaoAccount {

    private Profile profile;
    private String email;
  }

  @Getter
  @NoArgsConstructor
  public static class Profile {

    private String nickname;

    @JsonProperty("profile_image_url")
    private String profileImageUrl;
  }
}
