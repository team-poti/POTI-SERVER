package org.sopt.poti.domain.image.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.image.dto.request.PresignedUrlRequest;
import org.sopt.poti.domain.image.dto.response.ImagePresignedUrlsResponse;
import org.sopt.poti.domain.image.dto.response.PresignedUrlResponse;
import org.sopt.poti.global.common.ApiResponse;
import org.sopt.poti.global.common.SuccessStatus;
import org.sopt.poti.global.external.s3.S3Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
@Tag(name = "Image", description = "이미지 업로드 관련 API")
public class ImageController {

  private final S3Service s3Service;

  @GetMapping("/presigned-url")
  @Operation(summary = "Presigned URL 다중 발급", description = "S3에 이미지를 업로드하기 위한 Presigned URL을 요청 수(count)만큼 발급받습니다.")
  @ResponseStatus(HttpStatus.OK)
  public ApiResponse<ImagePresignedUrlsResponse> getPresignedUrl(
      @ModelAttribute @Valid PresignedUrlRequest request
  ) {
    List<PresignedUrlResponse> urls = s3Service.getPresignedUrls(
        request.type(),
        request.count(),
        request.extension()
    );
    return ApiResponse.success(SuccessStatus.OK, ImagePresignedUrlsResponse.of(urls));
  }
}
