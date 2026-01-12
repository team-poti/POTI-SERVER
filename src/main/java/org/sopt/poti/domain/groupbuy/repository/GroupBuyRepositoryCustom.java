package org.sopt.poti.domain.groupbuy.repository;

import java.util.List;
import org.sopt.poti.domain.home.dto.response.HomeGroupBuyItem; // Import HomeGroupBuyItem

public interface GroupBuyRepositoryCustom {
    List<String> findTitlesByKeyword(Long artistId, String keyword, int limit);

    List<HomeGroupBuyItem> findPopularTitlesByArtist(Long userId, Long artistId, int limit); // userId도 필요 (nickname 등)
    List<HomeGroupBuyItem> findPopularTitlesExcludingArtist(Long userId, Long artistId, int limit); // userId도 필요
}
