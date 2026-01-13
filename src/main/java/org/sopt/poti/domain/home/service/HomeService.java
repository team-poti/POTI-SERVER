package org.sopt.poti.domain.home.service;

import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.poti.domain.artist.service.ArtistService;
import org.sopt.poti.domain.groupbuy.repository.GroupBuyRepository;
import org.sopt.poti.domain.home.dto.response.HomeBanner;
import org.sopt.poti.domain.home.dto.response.HomeGroupBuyItem;
import org.sopt.poti.domain.home.dto.response.HomeResponse;
import org.sopt.poti.domain.user.entity.User;
import org.sopt.poti.domain.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class HomeService {

  private final UserService userService;
  private final ArtistService artistService;
  private final GroupBuyRepository groupBuyRepository;

  private static final int ITEM_LIMIT = 5;

  public HomeResponse getHomeData(Long userId) {
    User user = userService.getUserById(userId);

    String nickname = user.getNickname();
    String mainArtistName = null;

    List<HomeGroupBuyItem> myGroupItems;
    List<HomeGroupBuyItem> otherGroupItems;

    if (user.getFavoriteArtist() != null) {
      Long favoriteArtistId = user.getFavoriteArtist().getId();
      mainArtistName = user.getFavoriteArtist().getName();

      myGroupItems = groupBuyRepository.findPopularTitlesByArtist(favoriteArtistId,
          ITEM_LIMIT);
      otherGroupItems = groupBuyRepository.findPopularTitlesExcludingArtist(favoriteArtistId,
          ITEM_LIMIT);
    } else {
      log.info("유저 ID {}의 최애 아티스트가 설정되지 않아 전체 인기 상품을 조회합니다.", userId);
      myGroupItems = Collections.emptyList();
      otherGroupItems = groupBuyRepository.findPopularTitlesExcludingArtist(null, ITEM_LIMIT);
    }

    // TODO: 배너 로직 구현 필요 (현재는 더미 추후에 기획에 물어보고 변경할 예정)
    List<HomeBanner> banners = List.of(
        HomeBanner.builder().postId(1L)
            .imageUrl("https://poti.s3.ap-northeast-2.amazonaws.com/banners/banner1.jpg").build(),
        HomeBanner.builder().postId(2L)
            .imageUrl("https://poti.s3.ap-northeast-2.amazonaws.com/banners/banner2.jpg").build()
    );

    return HomeResponse.builder()
        .nickname(nickname)
        .mainArtist(mainArtistName)
        .myGroupItems(myGroupItems)
        .otherGroupItems(otherGroupItems)
        .banners(banners)
        .build();
  }
}
