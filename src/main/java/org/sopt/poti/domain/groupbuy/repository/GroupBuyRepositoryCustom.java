package org.sopt.poti.domain.groupbuy.repository;

import java.util.List;
import org.sopt.poti.domain.home.dto.response.HomeGroupBuyItem;

public interface GroupBuyRepositoryCustom {

  List<String> findTitlesByKeyword(Long artistId, String keyword, int limit);

  List<HomeGroupBuyItem> findPopularTitlesByArtist(Long artistId, int limit);

  List<HomeGroupBuyItem> findPopularTitlesExcludingArtist(Long artistId, int limit);

}
