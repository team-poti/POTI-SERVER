package org.sopt.poti.global.external.apple;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.poti.global.error.BusinessException;
import org.sopt.poti.global.error.ErrorStatus;
import org.sopt.poti.global.external.apple.dto.ApplePublicKeyResponse;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppleIdTokenValidator {

    private final ApplePublicKeyFeignClient applePublicKeyFeignClient;

    public Claims validate(String idToken) {
        try {
            String kid = extractKid(idToken);

            ApplePublicKeyResponse keysResponse = applePublicKeyFeignClient.getPublicKeys();
            ApplePublicKeyResponse.Key matchingKey = keysResponse.getKeys().stream()
                .filter(key -> key.getKid().equals(kid))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorStatus.INVALID_TOKEN));

            PublicKey publicKey = buildPublicKey(matchingKey);

            return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(idToken)
                .getPayload();

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.warn("Apple ID Token 검증 실패: {}", e.getMessage());
            throw new BusinessException(ErrorStatus.INVALID_TOKEN);
        }
    }

    private String extractKid(String idToken) {
        try {
            String headerBase64 = idToken.split("\\.")[0];
            String headerJson = new String(Base64.getUrlDecoder().decode(headerBase64));
            // {"alg":"RS256","kid":"abc123"} 형태에서 kid 추출
            Map<?, ?> header = new com.fasterxml.jackson.databind.ObjectMapper()
                .readValue(headerJson, Map.class);
            return (String) header.get("kid");
        } catch (Exception e) {
            throw new BusinessException(ErrorStatus.INVALID_TOKEN);
        }
    }

    private PublicKey buildPublicKey(ApplePublicKeyResponse.Key key) {
        try {
            byte[] nBytes = Base64.getUrlDecoder().decode(key.getN());
            byte[] eBytes = Base64.getUrlDecoder().decode(key.getE());
            RSAPublicKeySpec spec = new RSAPublicKeySpec(
                new BigInteger(1, nBytes),
                new BigInteger(1, eBytes)
            );
            return KeyFactory.getInstance("RSA").generatePublic(spec);
        } catch (Exception e) {
            throw new BusinessException(ErrorStatus.INVALID_TOKEN);
        }
    }
}
