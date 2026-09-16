package org.sopt.poti.domain.artist.dto.response;

import java.util.List;

public record ArtistTitlesResponse(
    List<ArtistTitleDto> artists
) {

  public record ArtistTitleDto(Long artistId, String name) {

  }

  public static ArtistTitlesResponse of(List<ArtistTitleDto> artists) {
    return new ArtistTitlesResponse(artists);
  }
}