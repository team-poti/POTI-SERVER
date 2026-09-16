package org.sopt.poti.global.external.google;

import org.sopt.poti.global.external.google.dto.GoogleTokenInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "google-token-client", url = "https://oauth2.googleapis.com")
public interface GoogleTokenFeignClient {

    @GetMapping("/tokeninfo")
    GoogleTokenInfoResponse getTokenInfo(@RequestParam("id_token") String idToken);
}
