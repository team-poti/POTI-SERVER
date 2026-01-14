package org.sopt.poti.domain.groupbuy.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.artist.entity.Artist;
import org.sopt.poti.domain.artist.entity.Member;
import org.sopt.poti.domain.artist.service.ArtistService;
import org.sopt.poti.domain.artist.service.MemberService;
import org.sopt.poti.domain.delivery.entity.DeliveryMethod;
import org.sopt.poti.domain.delivery.service.DeliveryService;
import org.sopt.poti.domain.groupbuy.dto.request.GroupBuyCreateRequest;
import org.sopt.poti.domain.groupbuy.dto.request.GroupBuyListRequest;
import org.sopt.poti.domain.groupbuy.dto.response.GroupBuyCreateResponse;
import org.sopt.poti.domain.groupbuy.dto.response.GroupBuyDetailResponse;
import org.sopt.poti.domain.groupbuy.dto.response.GroupBuyDetailResponse.ImageResponse;
import org.sopt.poti.domain.groupbuy.dto.response.GroupBuyDetailResponse.ParticipantResponse;
import org.sopt.poti.domain.groupbuy.dto.response.GroupBuyDetailResponse.ShippingResponse;
import org.sopt.poti.domain.groupbuy.dto.response.GroupBuyDetailResponse.UploaderResponse;
import org.sopt.poti.domain.groupbuy.dto.response.GroupBuyListResponse;
import org.sopt.poti.domain.groupbuy.dto.response.GroupBuyPotItem;
import org.sopt.poti.domain.groupbuy.dto.response.GroupBuyPotItem.UploaderInfo;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyOption;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyPost;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyPostStatus;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyShipping;
import org.sopt.poti.domain.groupbuy.entity.ItemImage;
import org.sopt.poti.domain.groupbuy.repository.GroupBuyRepository;
import org.sopt.poti.domain.order.entity.OrderItem;
import org.sopt.poti.domain.order.service.OrderService;
import org.sopt.poti.domain.review.service.ReviewService;
import org.sopt.poti.domain.user.entity.User;
import org.sopt.poti.domain.user.service.UserService;
import org.sopt.poti.global.error.BusinessException;
import org.sopt.poti.global.error.ErrorStatus;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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
  private final ReviewService reviewService;
  private final OrderService orderService;

  @Transactional
  public GroupBuyCreateResponse createGroupBuyPost(Long userId, GroupBuyCreateRequest request) {
    User leader = userService.getUserById(userId);

    Artist artist = artistService.getById(request.artistId());

    String representativeImageUrl =
        request.imageUrls().isEmpty() ? null : request.imageUrls().get(0);
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

  public List<String> searchTitles(Long artistId, String keyword) {
    Pageable pageable = PageRequest.of(0, 5);
    return groupBuyRepository.findTitlesByKeyword(artistId, keyword, pageable.getPageSize());
  }

  public GroupBuyDetailResponse getGroupBuyDetail(Long userId, Long groupBuyId) {
    // 2번의 추가 쿼리를 방지하기 위한 조회
    GroupBuyPost groupBuyPost = groupBuyRepository.findByIdWithUserAndArtist(groupBuyId)
        .orElseThrow(() -> new BusinessException(ErrorStatus.POST_NOT_FOUND));

    /**
     * 분철글 이미지 리스트
     */
    List<ImageResponse> imageResponseList = groupBuyPost.getImages().stream()
        .map(
            itemImage ->
                ImageResponse.builder()
                    .sortOrder(itemImage.getSortOrder())
                    .imageUrl(itemImage.getImageUrl())
                    .build()
        ).toList();

    /**
     * 해당 분철글 배송 옵션 리스트
     */
    List<ShippingResponse> shippingResponseList = groupBuyPost.getShippings().stream()
        .map(
            groupBuyShipping ->
                ShippingResponse.builder()
                    .shippingId(groupBuyShipping.getId())
                    .name(groupBuyShipping.getDeliveryMethod().getName())
                    .price(groupBuyShipping.getPrice())
                    .build()
        ).toList();

    /**
     * 총대 정보 가져오기
     */
    // 리뷰 개수 가져오기
    Integer reviewCount = reviewService.countReviewsForSeller(groupBuyPost.getLeader().getId());
    UploaderResponse uploaderResponse = UploaderResponse.builder()
        .userId(groupBuyPost.getLeader().getId())
        .nickname(groupBuyPost.getLeader().getNickname())
        .profileImage(groupBuyPost.getLeader().getProfileImageUrl())
        .rating(groupBuyPost.getLeader().getRatingAvg())
        .reviewCount(reviewCount)
        .build();

    /**
     * 참여자 리스트
     */
    // 구매한 order 참여자들 리스트
    List<GroupBuyOption> options = groupBuyPost.getOptions();
    List<Long> optionIds = options.stream().map(GroupBuyOption::getId).toList();

    List<ParticipantResponse> participantResponseList = Collections.emptyList();

    // 이 분철글에 옵션이 하나라도 있어야 주문이 가능
    if (!optionIds.isEmpty()) {
      List<OrderItem> orderItemsByOrderId = orderService.getOrderItemsByOptionIds(optionIds);

      // 참여자 기준으로 OrderItem 묶기
      Map<User, List<OrderItem>> itemsByUser = orderItemsByOrderId.stream()
          .collect(Collectors.groupingBy(item -> item.getOrder().getUser()));

      // 참여자 리스트 반환
      participantResponseList = itemsByUser.entrySet().stream()
          .map(entry -> {
            User user = entry.getKey();
            List<OrderItem> items = entry.getValue();

            // 해당 유저가 선택한 멤버옵션 이름 추출
            List<String> memberList = items.stream()
                .map(item -> item.getGroupBuyOption().getMember().getName())
                .toList();

            return ParticipantResponse.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .profileImage(user.getProfileImageUrl())
                .rating(user.getRatingAvg())
                .selectedMembers(memberList)
                .build();
          })
          .toList();
    }

    /**
     * 해당 분철글 최저가 계산
     */
    int minCost = groupBuyPost.getOptions().stream()
        .mapToInt(GroupBuyOption::getPrice)
        .min()
        .orElse(0);

    return GroupBuyDetailResponse.builder()
        .postId(groupBuyPost.getId())
        .title(groupBuyPost.getTitle())
        .content(groupBuyPost.getContent())
        .deadline(groupBuyPost.getRecruitDeadline())
        .uploadTime(groupBuyPost.getUpdatedAt())
        .totalCount(groupBuyPost.getGoalQuantity())
        .currentCount(groupBuyPost.getCurrentQuantity())
        .artist(groupBuyPost.getArtist().getName())
        .artistId(groupBuyPost.getArtist().getId())
        .images(imageResponseList)
        .isMyPost(groupBuyPost.getLeader().getId().equals(userId))
        .status(groupBuyPost.getStatus().name())
        .shippingOptions(shippingResponseList)
        .uploader(uploaderResponse)
        .participants(participantResponseList)
        .price(minCost)
        .build();
  }

  /**
   * 특정 상품에 해당하는 팟들을 조회합니다.
   */
  public GroupBuyListResponse getGroupBuyListByPostTitle(GroupBuyListRequest request,
      Pageable pageable) {

    Slice<GroupBuyPost> postsSlice = groupBuyRepository.findGroupBuyList(request, pageable);

    List<GroupBuyPotItem> potItems = postsSlice.getContent().stream()
        .map(post -> {
          // 최저가 계산
          int minPrice = post.getOptions().stream()
              .mapToInt(GroupBuyOption::getPrice)
              .min()
              .orElse(0);

          // 남은 멤버 리스트
          List<String> availableMembers = calculateAvailableMembers(post.getOptions());

          // 총대 정보
          UploaderInfo uploaderInfo = UploaderInfo.builder()
              .userId(post.getLeader().getId())
              .nickname(post.getLeader().getNickname())
              .profileImage(post.getLeader().getProfileImageUrl())
              .rating(post.getLeader().getRatingAvg())
              .build();

          return GroupBuyPotItem.builder()
              .potId(post.getId())
              .price(minPrice)
              .thumbnailUrl(post.getRepresentativeImageUrl())
              .currentCount(post.getCurrentQuantity())
              .totalCount(post.getGoalQuantity())
              .status(post.getStatus())
              .availableMembers(availableMembers)
              .uploader(uploaderInfo)
              .build();
        })
        .toList();

    String postTitle = null; // itemName 대신 postTitle
    String artistName = null;
    Long artistId = null; // artistId 추가
    if (!postsSlice.getContent().isEmpty()) {
      GroupBuyPost firstPost = postsSlice.getContent().get(0);
      postTitle = firstPost.getTitle();
      artistId = firstPost.getArtist().getId(); // artistId 추가
      artistName = firstPost.getArtist().getName();
    }

    return GroupBuyListResponse.of(
        postTitle,
        artistId,
        artistName,
        postsSlice.getNumber(), // 현재 페이지 번호
        postsSlice.hasNext(),
        potItems
    );
  }

  // 남은 멤버 리스트 계산 헬퍼 메서드 (기존 calculateAvailableMembers 재사용)
  private List<String> calculateAvailableMembers(List<GroupBuyOption> options) {
    if (options.isEmpty()) {
      return Collections.emptyList();
    }
    List<Long> optionIds = options.stream().map(GroupBuyOption::getId).toList();

    List<OrderItem> orderedItems = orderService.getOrderItemsByOptionIds(optionIds);

    List<Long> soldOptionIds = orderedItems.stream()
        .map(item -> item.getGroupBuyOption().getId())
        .distinct()
        .toList();

    return options.stream()
        .filter(option -> !soldOptionIds.contains(option.getId()))
        .map(option -> option.getMember().getName())
        .toList();
  }

  public int countByLeader_Id(Long userId) {
    return groupBuyRepository.countByLeader_Id(userId);
  }

  public int countByLeader_IdAndStatusIn(Long userId, List<GroupBuyPostStatus> statuses) {
    return groupBuyRepository.countByLeader_IdAndStatusIn(userId, statuses);
  }
}
