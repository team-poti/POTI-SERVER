package org.sopt.poti.domain.image.dto.response;

import java.util.List;

public record ImagePresignedUrlsResponse(
    List<PresignedUrlResponse> urls
) {
    public static ImagePresignedUrlsResponse of(List<PresignedUrlResponse> urls) {
        return new ImagePresignedUrlsResponse(urls);
    }
}
