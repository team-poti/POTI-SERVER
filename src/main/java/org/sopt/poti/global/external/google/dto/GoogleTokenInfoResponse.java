package org.sopt.poti.global.external.google.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GoogleTokenInfoResponse {

    private String sub;
    private String email;
    // /tokeninfo 엔드포인트는 aud만 반환. name/picture는 미포함
    private String aud;
}
