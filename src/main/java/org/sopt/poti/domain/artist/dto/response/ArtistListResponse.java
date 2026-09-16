package org.sopt.poti.domain.artist.dto.response;


import java.util.List;

public record ArtistListResponse(
        List<ArtistItemResponse> artists
) {
    public record ArtistItemResponse(
            Long artistId,
            String name,
            String logoImageUrl
    ) {}
}