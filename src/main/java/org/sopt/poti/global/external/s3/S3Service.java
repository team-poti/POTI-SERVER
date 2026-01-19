package org.sopt.poti.global.external.s3;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.poti.domain.image.dto.response.PresignedUrlResponse;
import org.sopt.poti.domain.image.entity.ImageDirectory;
import org.sopt.poti.global.error.BusinessException;
import org.sopt.poti.global.error.ErrorStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

  private final S3Presigner s3Presigner;

  @Value("${spring.cloud.aws.s3.bucket}")
  private String bucketName;

  public List<PresignedUrlResponse> getPresignedUrls(ImageDirectory directory, List<String> extensions) {
    List<PresignedUrlResponse> responses = new ArrayList<>();

    for (String extension : extensions) {
      String path = createPath(directory.getPrefix(), extension);
      String presignedUrl = generatePresignedUrl(path, extension); // Content-Type을 위해 extension 전달

      responses.add(PresignedUrlResponse.of(path, presignedUrl));
    }

    return responses;
  }

  private String generatePresignedUrl(String key, String extension) {
    try {
      PutObjectRequest objectRequest = PutObjectRequest.builder()
          .bucket(bucketName)
          .key(key)
          .contentType(getContentType(extension)) // Content-Type 설정
          .build();

      PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
          .signatureDuration(Duration.ofMinutes(5)) // 5분간 유효
          .putObjectRequest(objectRequest)
          .build();

      return s3Presigner.presignPutObject(presignRequest).url().toString();
    } catch (S3Exception e) {
      log.error("S3 Presigned URL 생성 실패: {}", e.getMessage());
      throw new BusinessException(ErrorStatus.EXTERNAL_API_ERROR);
    } catch (Exception e) {
      log.error("S3 Presigned URL 생성 중 알 수 없는 오류: {}", e.getMessage());
      throw new BusinessException(ErrorStatus.INTERNAL_SERVER_ERROR);
    }
  }

  private String createPath(String prefix, String extension) {
    String fileId = UUID.randomUUID().toString();
    LocalDate now = LocalDate.now();
    // 예: posts/2026/01/uuid.jpg
    return String.format("%s/%d/%02d/%s.%s",
        prefix, now.getYear(), now.getMonthValue(), fileId, extension);
  }

  // 확장자에 따른 Content-Type 반환 헬퍼 메서드
  private String getContentType(String extension) {
    return switch (extension.toLowerCase()) {
      case "jpg", "jpeg" -> "image/jpeg";
      case "png" -> "image/png";
      case "gif" -> "image/gif";
      case "webp" -> "image/webp";
      case "heic" -> "image/heic"; // HEIC mime type, S3가 지원하는지 확인 필요
      default -> "application/octet-stream"; // 기본값
    };
  }
}
