package org.sopt.poti.domain.groupbuy.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.artist.entity.Artist;
import org.sopt.poti.domain.artist.entity.Member;
import org.sopt.poti.domain.artist.service.ArtistService;
import org.sopt.poti.domain.artist.service.MemberService;
import org.sopt.poti.domain.delivery.entity.DeliveryMethod;
import org.sopt.poti.domain.delivery.service.DeliveryService;
import org.sopt.poti.domain.groupbuy.dto.request.GroupBuyCreateRequest;
import org.sopt.poti.domain.groupbuy.dto.response.GroupBuyCreateResponse;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyOption;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyPost;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyShipping;
import org.sopt.poti.domain.groupbuy.entity.ItemImage;
import org.sopt.poti.domain.groupbuy.repository.GroupBuyRepository;
import org.sopt.poti.domain.user.entity.User;
import org.sopt.poti.domain.user.service.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupBuyService {

  private final GroupBuyRepository groupBuyRepository;
  private final UserService userService;
  private final ArtistService artistService;
  private final MemberService memberService;
  private final DeliveryService deliveryService;

  @Transactional
  public GroupBuyCreateResponse createGroupBuyPost(Long userId, GroupBuyCreateRequest request) {
    User leader = userService.getUserById(userId);

    Artist artist = artistService.getById(request.artistId());

    // 대표 이미지 URL (첫 번째 이미지)
    String representativeImageUrl = request.imageUrls().isEmpty() ? null : request.imageUrls().get(0);
    // 목표 수량 = 옵션 개수
    int goalQuantity = request.options().size();

    GroupBuyPost groupBuyPost = GroupBuyPost.create(
        request.title(),
        request.content(),
        request.deadline(),
        request.bankName(),
        request.accountNumber(),
        goalQuantity,
        representativeImageUrl,
        leader,
        artist
    );

    request.options().forEach(optionRequest -> {
      Member member = memberService.getMemberById(optionRequest.memberId());
      GroupBuyOption option = GroupBuyOption.create(optionRequest.price(), member);
      groupBuyPost.addOption(option);
    });

    request.shippings().forEach(shippingRequest -> {
      DeliveryMethod deliveryMethod = deliveryService.getDeliveryMethodById(
          shippingRequest.deliveryMethodId());
      GroupBuyShipping shipping = GroupBuyShipping.create(shippingRequest.price(), deliveryMethod);
      groupBuyPost.addShipping(shipping);
    });

    List<String> imageUrls = request.imageUrls();
    for (int i = 0; i < imageUrls.size(); i++) {
      ItemImage image = ItemImage.create(imageUrls.get(i), i);
      groupBuyPost.addImage(image);
    }

    groupBuyRepository.save(groupBuyPost);

    return GroupBuyCreateResponse.of(groupBuyPost.getId());
  }

  // 상품명 자동완성
  public List<String> searchTitles(Long artistId, String keyword) {
    Pageable pageable = PageRequest.of(0, 5); // 최대 5개만 조회
    return groupBuyRepository.findTitlesByKeyword(artistId, keyword, pageable.getPageSize());
  }
}
