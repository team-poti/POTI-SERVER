package org.sopt.poti.domain.feed.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedService {

  private final UserService userService;
  private final GroupBuyRepository groupBuyRepository;

  public FeedResponse getFeed(Long userId, Long artistId, String sort, Pageable pageable) {
    String nickname = null;
    String mainArtist = null;
    Long mainArtistId = null;

    if (userId != null) {
      User user = userService.getUserById(userId);
      nickname = user.getNickname();
      mainArtist = user.getFavoriteArtist() != null ? user.getFavoriteArtist().getName() : null;
      mainArtistId = user.getFavoriteArtist() != null ? user.getFavoriteArtist().getId() : null;
    }

    log.info("1st :{}", sort);
    FeedSearchCondition condition = FeedSearchCondition.builder()
        .artistId(artistId)
        .sort(sort)
        .pageable(pageable)
        .build();

    log.info("2st :{}", condition.sort());
    Slice<FeedGroupItem> feedItems = groupBuyRepository.findFeedItems(condition, pageable);

    return FeedResponse.of(nickname, mainArtist, mainArtistId, feedItems.hasNext(),
        feedItems.getContent());
  }
}
