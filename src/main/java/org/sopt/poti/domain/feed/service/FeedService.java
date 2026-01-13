package org.sopt.poti.domain.feed.service;

import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.feed.dto.request.FeedSearchCondition;
import org.sopt.poti.domain.feed.dto.response.FeedGroupItem;
import org.sopt.poti.domain.feed.dto.response.FeedResponse;
import org.sopt.poti.domain.groupbuy.repository.GroupBuyRepository;
import org.sopt.poti.domain.user.entity.User;
import org.sopt.poti.domain.user.service.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedService {

  private final UserService userService;
  private final GroupBuyRepository groupBuyRepository;

  public FeedResponse getFeed(Long userId, Long artistId, String sort, Pageable pageable) {
    User user = userService.getUserById(userId);

    FeedSearchCondition condition = FeedSearchCondition.builder()
        .artistId(artistId) // 파라미터가 없으면 null
        .sort(sort)
        .pageable(pageable)
        .build();

    Slice<FeedGroupItem> feedItems = groupBuyRepository.findFeedItems(condition, pageable);

    String mainArtist =
        user.getFavoriteArtist() != null ? user.getFavoriteArtist().getName() : null;

    return FeedResponse.of(
        user.getNickname(),
        mainArtist,
        feedItems.hasNext(),
        feedItems.getContent()
    );
  }
}
