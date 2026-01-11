package org.sopt.poti.domain.image.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.sopt.poti.domain.image.entity.ImageDirectory;

public record PresignedUrlRequest(
    @NotNull(message = "이미지 용도(type)는 필수입니다.")
    ImageDirectory type,

    @Min(value = 1, message = "이미지 개수는 1개 이상이어야 합니다.")
    @Max(value = 5, message = "이미지 개수는 5개 이하여야 합니다.")
    int count,
    
    @NotBlank(message = "파일 확장자는 필수입니다.")
    String extension
) {
}
