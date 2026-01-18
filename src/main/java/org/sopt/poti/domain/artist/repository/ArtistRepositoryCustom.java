package org.sopt.poti.domain.artist.repository;

import java.util.List;
import org.sopt.poti.domain.artist.dto.response.ArtistTitlesResponse;

public interface ArtistRepositoryCustom {

  List<ArtistTitlesResponse.ArtistTitleDto> findByPrefix(String keyword, int limit);
}
