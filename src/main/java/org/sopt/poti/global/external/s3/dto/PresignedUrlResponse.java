package org.sopt.poti.global.external.s3.dto;

public record PresignedUrlResponse(
    String presignedUrl, // 업로드용 URL (PUT)
    String imageUrl      // 조회/저장용 URL (DB에 저장할 값)
) {
    public static PresignedUrlResponse of(String presignedUrl, String imageUrl) {
        return new PresignedUrlResponse(presignedUrl, imageUrl);
    }
}
