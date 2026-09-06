package org.sopt.poti.domain.home.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.poti.domain.artist.service.ArtistService;
import org.sopt.poti.domain.banner.repository.BannerRepository;
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
  private final BannerRepository bannerRepository;

  private static final int ITEM_LIMIT = 5;

  public HomeResponse getHomeData(Long userId) {
    String nickname = null;
    String mainArtistName = null;
    Long mainArtistId = null;

    List<HomeGroupBuyItem> myGroupItems;
    List<HomeGroupBuyItem> otherGroupItems;

    if (userId != null) {
      User user = userService.getUserById(userId);
      nickname = user.getNickname();
      mainArtistName = user.getFavoriteArtist() != null ? user.getFavoriteArtist().getName() : null;
      mainArtistId = user.getFavoriteArtist() != null ? user.getFavoriteArtist().getId() : null;
    }

    if (mainArtistId != null) {
      myGroupItems = groupBuyRepository.findPopularTitlesByArtist(mainArtistId, ITEM_LIMIT);

      if (myGroupItems.isEmpty()) {
        myGroupItems = groupBuyRepository.findPopularTitlesByArtist(null, ITEM_LIMIT);
      }

      otherGroupItems = groupBuyRepository.findPopularTitlesExcludingArtist(mainArtistId,
          "RANDOM", ITEM_LIMIT);
    } else {
      log.info("유저 ID {}의 최애 아티스트가 설정되지 않아 전체 인기 상품을 조회합니다.", userId);

      myGroupItems = groupBuyRepository.findPopularTitlesByArtist(null,
          ITEM_LIMIT); // 전체 인기 상품 (최애 없을 때 상단)
      otherGroupItems = groupBuyRepository.findPopularTitlesExcludingArtist(null, "RANDOM",
          ITEM_LIMIT);
    }

    List<HomeBanner> banners = bannerRepository.findByActiveTrueOrderBySortOrderAsc()
        .stream()
        .map(HomeBanner::from)
        .toList();

    return HomeResponse.builder()
        .nickname(nickname)
        .mainArtist(mainArtistName)
        .mainArtistId(mainArtistId)
        .myGroupItems(myGroupItems)
        .otherGroupItems(otherGroupItems)
        .banners(banners)
        .build();
  }
}