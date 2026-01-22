package org.sopt.poti.domain.groupbuy.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.poti.global.util.AesUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Converter
@Component // Spring Bean으로 등록하여 AesUtils 주입 가능하도록 함
@RequiredArgsConstructor
public class AccountNumberConverter implements AttributeConverter<String, String> {

  private final AesUtils aesUtils;

  @Override
  public String convertToDatabaseColumn(String attribute) {
    if (attribute == null || attribute.isBlank()) {
      return null;
    }
    return aesUtils.encrypt(attribute); // 저장할 땐 암호화
  }

  @Override
  public String convertToEntityAttribute(String dbData) {
    if (dbData == null || dbData.isBlank()) {
      return null;
    }
    try {
      return aesUtils.decrypt(dbData); // 복호화 시도
    } catch (Exception e) {
      // 복호화 실패 시 (기존 평문 데이터일 가능성)
      log.warn("데이터 복호화 실패. 평문으로 간주하고 반환합니다: {}", dbData);
      return dbData;
    }
  }
}
