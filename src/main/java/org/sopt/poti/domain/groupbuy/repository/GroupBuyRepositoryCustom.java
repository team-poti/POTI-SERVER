package org.sopt.poti.domain.groupbuy.repository;

import java.util.List;
import org.sopt.poti.domain.feed.dto.request.FeedSearchCondition;
import org.sopt.poti.domain.feed.dto.response.FeedGroupItem;
import org.sopt.poti.domain.home.dto.response.HomeGroupBuyItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface GroupBuyRepositoryCustom {

  List<String> findTitlesByKeyword(Long artistId, String keyword, int limit);

  List<HomeGroupBuyItem> findPopularTitlesByArtist(Long artistId, int limit);

  List<HomeGroupBuyItem> findPopularTitlesExcludingArtist(Long artistId, int limit);

  Slice<FeedGroupItem> findFeedItems(FeedSearchCondition condition, Pageable pageable);
}
