package org.sopt.poti.global.external.google.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GoogleTokenInfoResponse {

    private String sub;
    private String email;
    private String name;

    @JsonProperty("picture")
    private String profileImageUrl;
}
