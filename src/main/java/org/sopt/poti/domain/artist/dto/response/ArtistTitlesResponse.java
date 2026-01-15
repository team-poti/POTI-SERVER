package org.sopt.poti.domain.artist.dto.response;

import java.util.List;

public record ArtistTitlesResponse(
    List<String> titles
) {

  public static ArtistTitlesResponse of(List<String> titles) {
    return new ArtistTitlesResponse(titles);
  }
}