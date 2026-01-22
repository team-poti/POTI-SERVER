package org.sopt.poti.global.util;

import jakarta.annotation.PostConstruct;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.sopt.poti.global.error.BusinessException;
import org.sopt.poti.global.error.ErrorStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AesUtils {

  @Value("${security.encryption-key}") // 32byte (256bit) 키 필요
  private String secretKey;

  private static final String ALGORITHM = "AES/GCM/NoPadding";
  private static final int IV_LENGTH = 12; // GCM 권장 IV 길이
  private static final int TAG_LENGTH = 128; // AES/GCM에서 사용되는 인증 태그 길이

  private SecretKeySpec keySpec;

  @PostConstruct
  public void init() {
    if (secretKey == null) {
      log.error("암호화 키(security.encryption-key)는 32바이트(256비트)여야 합니다. 현재 길이: null");
      throw new BusinessException(ErrorStatus.AES_KEY_LENGTH);
    }
    byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
    if (keyBytes.length != 32) {
      log.error("암호화 키(security.encryption-key)는 32바이트(256비트)여야 합니다. 현재 길이: {}", keyBytes.length);
      throw new BusinessException(ErrorStatus.AES_KEY_LENGTH);
    }
    this.keySpec = new SecretKeySpec(keyBytes, "AES");
  }

  // 암호화
  public String encrypt(String plainText) {
    try {
      byte[] iv = new byte[IV_LENGTH];
      new SecureRandom().nextBytes(iv); // 매번 랜덤 IV 생성 (보안 핵심)

      Cipher cipher = Cipher.getInstance(ALGORITHM);
      GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH, iv);

      cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec);
      byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

      // IV와 암호문을 합쳐서 저장 (복호화 할 때 IV가 필요함)
      ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + cipherText.length);
      byteBuffer.put(iv);
      byteBuffer.put(cipherText);

      return Base64.getEncoder().encodeToString(byteBuffer.array());
    } catch (Exception e) {
      log.error("암호화 실패: {}", e.getMessage(), e);
      throw new BusinessException(ErrorStatus.ENCRYPTION_ERROR);
    }
  }

  // 복호화
  public String decrypt(String cipherText) {
    try {
      byte[] decoded = Base64.getDecoder().decode(cipherText);

      // IV 분리
      ByteBuffer byteBuffer = ByteBuffer.wrap(decoded);
      byte[] iv = new byte[IV_LENGTH];
      byteBuffer.get(iv);

      // 실제 암호문 분리
      byte[] encryptedContent = new byte[byteBuffer.remaining()];
      byteBuffer.get(encryptedContent);

      Cipher cipher = Cipher.getInstance(ALGORITHM);
      GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH, iv);

      cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);
      byte[] plainText = cipher.doFinal(encryptedContent);

      return new String(plainText, StandardCharsets.UTF_8);
    } catch (Exception e) {
      log.warn("복호화 실패: {} (평문 데이터일 가능성)", e.getMessage());
      // 복호화 실패 시 (기존 평문 데이터일 가능성) -> 여기서는 에러를 다시 던지고, 컨버터에서 방어로직
      throw new BusinessException(ErrorStatus.DECRYPTION_ERROR);
    }
  }
}
