package org.sopt.poti.global.external.apple;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppleIdTokenValidator {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String APPLE_ISS = "https://appleid.apple.com";

    private final ApplePublicKeyFeignClient applePublicKeyFeignClient;

    @Value("${apple.bundle-id}")
    private String appleBundleId;

    public Claims validate(String idToken) {
        try {
            String kid = extractKid(idToken);

            ApplePublicKeyResponse keysResponse = fetchApplePublicKeys();
            ApplePublicKeyResponse.Key matchingKey = keysResponse.getKeys().stream()
                .filter(key -> key.getKid().equals(kid))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorStatus.INVALID_TOKEN));

            PublicKey publicKey = buildPublicKey(matchingKey);

            return Jwts.parser()
                .verifyWith(publicKey)
                .requireIssuer(APPLE_ISS)
                .requireAudience(appleBundleId)
                .clockSkewSeconds(60)
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

    private ApplePublicKeyResponse fetchApplePublicKeys() {
        try {
            return applePublicKeyFeignClient.getPublicKeys();
        } catch (FeignException e) {
            log.error("Apple 공개키 조회 실패 (status={}): {}", e.status(), e.getMessage());
            throw new BusinessException(ErrorStatus.EXTERNAL_API_ERROR);
        }
    }

    private String extractKid(String idToken) {
        try {
            String headerBase64 = idToken.split("\\.")[0];
            String headerJson = new String(Base64.getUrlDecoder().decode(headerBase64));
            Map<?, ?> header = objectMapper.readValue(headerJson, Map.class);
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
