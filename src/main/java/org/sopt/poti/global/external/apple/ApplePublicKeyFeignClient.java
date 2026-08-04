package org.sopt.poti.global.external.apple;

import org.sopt.poti.global.external.apple.dto.ApplePublicKeyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "apple-public-key-client", url = "https://appleid.apple.com")
public interface ApplePublicKeyFeignClient {

    @GetMapping("/auth/keys")
    ApplePublicKeyResponse getPublicKeys();
}
