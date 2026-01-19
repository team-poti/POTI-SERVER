package org.sopt.poti.domain.image.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.sopt.poti.domain.image.entity.ImageDirectory;

public record PresignedUrlRequest(
    @Schema(description = "이미지 용도", example = "POST")
    @NotNull(message = "이미지 용도(type)는 필수입니다.")
    ImageDirectory type,

    @Schema(description = "업로드할 이미지들의 확장자 리스트", example = "[\"jpg\", \"png\", \"jpeg\"]")
    @NotEmpty(message = "확장자는 최소 1개 이상 필수입니다.")
    @Size(min = 1, max = 5, message = "이미지는 최대 5개까지 업로드할 수 있습니다.")
    List<@NotBlank @Pattern(regexp = "^(jpg|jpeg|png|heic|webp)$", message = "지원하지 않는 확장자입니다.") String> extensions
) {
}
