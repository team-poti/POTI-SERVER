package org.sopt.poti.domain.artist.repository;

import java.util.List;

public interface ArtistRepositoryCustom {

  List<String> findNamesByPrefix(String keyword, int limit);
}
