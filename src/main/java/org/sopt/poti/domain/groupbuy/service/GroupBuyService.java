package org.sopt.poti.domain.groupbuy.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import org.sopt.poti.domain.groupbuy.dto.response.GroupBuyPostOptionResponse;
import org.sopt.poti.domain.groupbuy.dto.response.GroupBuyPostOptionResponse.GroupBuyPostDeliveryOption;
import org.sopt.poti.domain.groupbuy.dto.response.GroupBuyPostOptionResponse.GroupBuyPostMemberOption;
import org.sopt.poti.domain.groupbuy.dto.response.GroupBuyPotItem;
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
      GroupBuyShipping shipping = GroupBuyShipping.create(shippingRequest.price(), deliveryMethod,
          null);
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
    Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 5);
    return groupBuyRepository.findTitlesByKeyword(artistId, keyword, pageable.getPageSize());
  }

  public GroupBuyDetailResponse getGroupBuyDetail(Long userId, Long groupBuyId) {
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
    List<GroupBuyOption> options = groupBuyPost.getOptions();
    List<Long> optionIds = options.stream().map(GroupBuyOption::getId).toList();

    List<ParticipantResponse> participantResponseList = Collections.emptyList();

    if (!optionIds.isEmpty()) {
      List<OrderItem> orderItems = orderService.getOrderItemsByOptionIds(optionIds);

      if (!orderItems.isEmpty()) {
        Map<User, List<OrderItem>> itemsByUser = orderItems.stream()
            .collect(Collectors.groupingBy(item -> item.getOrder().getUser()));

        participantResponseList = itemsByUser.entrySet().stream()
            .map(entry -> {
              User user = entry.getKey();
              List<OrderItem> items = entry.getValue();

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

    // 1. 모든 게시글의 옵션 ID 수집
    List<Long> allOptionIds = postsSlice.getContent().stream()
        .flatMap(post -> post.getOptions().stream())
        .map(GroupBuyOption::getId)
        .toList();

    // 2. 한 번의 쿼리로 모든 주문 내역 조회 (판매된 옵션 확인)
    List<OrderItem> allOrderItems = allOptionIds.isEmpty()
        ? Collections.emptyList()
        : orderService.getOrderItemsByOptionIds(allOptionIds);

    // 3. 판매된 옵션 ID Set 생성
    Set<Long> soldOptionIds = allOrderItems.stream()
        .map(item -> item.getGroupBuyOption().getId())
        .collect(Collectors.toSet());

    List<GroupBuyPotItem> potItems = postsSlice.getContent().stream()
        .map(post -> {
          int minPrice = post.getOptions().stream()
              .mapToInt(GroupBuyOption::getPrice)
              .min()
              .orElse(0);

          // 남은 멤버 리스트
          List<String> availableMembers = post.getOptions().stream()
              .filter(option -> !soldOptionIds.contains(option.getId()))
              .map(option -> option.getMember().getName())
              .toList();

          GroupBuyPotItem.UploaderInfo uploaderInfo = GroupBuyPotItem.UploaderInfo.builder()
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

    String postTitle = null;
    String artistName = null;
    Long artistId = null;
    if (!postsSlice.getContent().isEmpty()) {
      GroupBuyPost firstPost = postsSlice.getContent().get(0);
      postTitle = firstPost.getTitle();
      artistId = firstPost.getArtist().getId();
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

  public GroupBuyPostOptionResponse getGroupBuyPostOptionResponse(Long postId) {
    // 게시글 및 옵션 전체 조회
    GroupBuyPost post = groupBuyRepository.findById(postId)
        .orElseThrow(() -> new BusinessException(ErrorStatus.POST_NOT_FOUND));
    List<GroupBuyOption> options = post.getOptions();

    // option 식별자들 추출
    List<Long> optionIds = options.stream().map(GroupBuyOption::getId).toList();

    // 이미 주문된(팔린) 옵션 조회 (OrderItem이 존재하는 옵션)
    List<OrderItem> soldItems = orderService.getOrderItemsByOptionIds(optionIds);
    Set<Long> soldOptionIds = soldItems.stream()
        .map(item -> item.getGroupBuyOption().getId())
        .collect(Collectors.toSet());

    // 이미 팔린 옵션 제외
    List<GroupBuyOption> memberList = options.stream()
        .filter(option -> !soldOptionIds.contains(option.getId()))
        .toList();

    List<GroupBuyPostMemberOption> groupBuyPostMemberOptions = memberList.stream().map(
            member ->
                new GroupBuyPostMemberOption(
                    member.getId(),
                    member.getMember().getName(),
                    member.getPrice())
        )
        .toList();

    List<GroupBuyPostDeliveryOption> deliveryOptions = post.getShippings().stream().map(
            delivery ->
                new GroupBuyPostDeliveryOption(
                    delivery.getId(),
                    delivery.getDeliveryMethod().getName(),
                    delivery.getPrice())
        )
        .toList();

    return new GroupBuyPostOptionResponse(groupBuyPostMemberOptions, deliveryOptions);
  }

  public int countByLeader_Id(Long userId) {
    return groupBuyRepository.countByLeader_Id(userId);
  }

  public int countByLeader_IdAndStatusIn(Long userId, List<GroupBuyPostStatus> statuses) {
    return groupBuyRepository.countByLeader_IdAndStatusIn(userId, statuses);
  }
}