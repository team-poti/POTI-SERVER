package org.sopt.poti.domain.image.dto.response;

import lombok.Builder;

@Builder
public record PresignedUrlResponse(
    String fileName, // DB 저장용 경로 (Key)
    String url       // 업로드용 URL
) {
    public static PresignedUrlResponse of(String fileName, String url) {
        return PresignedUrlResponse.builder()
                .fileName(fileName)
                .url(url)
                .build();
    }
}
