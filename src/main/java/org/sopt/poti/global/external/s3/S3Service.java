package org.sopt.poti.global.external.s3;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.image.dto.response.PresignedUrlResponse;
import org.sopt.poti.domain.image.entity.ImageDirectory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@RequiredArgsConstructor
public class S3Service {

  private final S3Presigner s3Presigner;

  @Value("${spring.cloud.aws.s3.bucket}")
  private String bucketName;

  public List<PresignedUrlResponse> getPresignedUrls(ImageDirectory directory, int count,
      String extension) {
    List<PresignedUrlResponse> responses = new ArrayList<>();

    // count 만큼 Presigned URL 발급을 위한 반복문
    for (int i = 0; i < count; i++) {
      String path = createPath(directory.getPrefix(), extension);
      String presignedUrl = generatePresignedUrl(path);

      responses.add(PresignedUrlResponse.of(path, presignedUrl));
    }

    return responses;
  }

  private String generatePresignedUrl(String key) {
    PutObjectRequest objectRequest = PutObjectRequest.builder()
        .bucket(bucketName)
        .key(key)
        .build();

    PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
        .signatureDuration(Duration.ofMinutes(5)) // 5분간 유효
        .putObjectRequest(objectRequest)
        .build();

    return s3Presigner.presignPutObject(presignRequest).url().toString();
  }

  private String createPath(String prefix, String extension) {
    String fileId = UUID.randomUUID().toString();
    LocalDate now = LocalDate.now();
    // 예: posts/2026/01/uuid.jpg
    return String.format("%s/%d/%02d/%s.%s",
        prefix, now.getYear(), now.getMonthValue(), fileId, extension);
  }
}
