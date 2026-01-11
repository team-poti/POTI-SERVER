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

  public List<PresignedUrlResponse> getPresignedUrls(ImageDirectory directory, int count,
      String extension) {
    List<PresignedUrlResponse> responses = new ArrayList<>();

    for (int i = 0; i < count; i++) {
      String path = createPath(directory.getPrefix(), extension);
      String presignedUrl = generatePresignedUrl(path);

      responses.add(PresignedUrlResponse.of(path, presignedUrl));
    }

    return responses;
  }

  private String generatePresignedUrl(String key) {
    try {
      PutObjectRequest objectRequest = PutObjectRequest.builder()
          .bucket(bucketName)
          .key(key)
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
}
