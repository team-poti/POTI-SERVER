package org.sopt.poti.global.external.apple.dto;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ApplePublicKeyResponse {

    private List<Key> keys;

    @Getter
    @NoArgsConstructor
    public static class Key {
        private String kty;
        private String kid;
        private String use;
        private String alg;
        private String n;
        private String e;
    }
}
