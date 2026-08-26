package org.sopt.poti.domain.admin.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.artist.entity.Artist;
import org.sopt.poti.domain.artist.repository.ArtistRepository;
import org.sopt.poti.domain.artist.repository.MemberRepository;
import org.sopt.poti.domain.auth.repository.RefreshTokenRepository;
import org.sopt.poti.domain.fcmtoken.service.FcmTokenService;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyPost;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyPostStatus;
import org.sopt.poti.domain.groupbuy.repository.GroupBuyRepository;
import org.sopt.poti.domain.order.repository.OrderRepository;
import org.sopt.poti.domain.user.entity.User;
import org.sopt.poti.domain.user.entity.UserStatus;
import org.sopt.poti.domain.user.repository.UserRepository;
import org.sopt.poti.global.error.BusinessException;
import org.sopt.poti.global.error.ErrorStatus;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

  private final UserRepository userRepository;
  private final GroupBuyRepository groupBuyRepository;
  private final OrderRepository orderRepository;
  private final RefreshTokenRepository refreshTokenRepository;
  private final FcmTokenService fcmTokenService;
  private final ArtistRepository artistRepository;
  private final MemberRepository memberRepository;

  public long countArtists() {
    return artistRepository.count();
  }

  public List<Artist> getArtists() {
    return artistRepository.findAll(Sort.by("name"));
  }

  public Map<Long, Long> getArtistPostCounts() {
    return artistRepository.findAll().stream()
        .collect(Collectors.toMap(Artist::getId, a -> groupBuyRepository.countByArtist_Id(a.getId())));
  }

  @Transactional
  public void createArtist(String name, String logoImageUrl) {
    artistRepository.save(Artist.create(name, logoImageUrl.isBlank() ? null : logoImageUrl));
  }

  @Transactional
  public void deleteArtist(Long artistId) {
    Artist artist = artistRepository.findById(artistId)
        .orElseThrow(() -> new BusinessException(ErrorStatus.ARTIST_NOT_FOUND));
    if (groupBuyRepository.existsByArtist_Id(artistId)) {
      throw new BusinessException(ErrorStatus.ARTIST_HAS_POSTS);
    }
    memberRepository.deleteByArtist(artist);
    artistRepository.delete(artist);
  }

  public long countUsers() {
    return userRepository.count();
  }

  public long countPosts() {
    return groupBuyRepository.count();
  }

  public long countOrders() {
    return orderRepository.count();
  }

  public Page<User> getUsers(Pageable pageable) {
    return userRepository.findAll(pageable);
  }

  public Page<GroupBuyPost> getPosts(GroupBuyPostStatus status, Pageable pageable) {
    if (status != null) {
      return groupBuyRepository.findByStatus(status, pageable);
    }
    return groupBuyRepository.findAll(pageable);
  }

  @Transactional
  public void suspendUser(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new BusinessException(ErrorStatus.USER_NOT_FOUND));
    if (user.getStatus() == UserStatus.WITHDRAWN) {
      throw new BusinessException(ErrorStatus.USER_NOT_FOUND);
    }
    user.suspend();
    refreshTokenRepository.deleteById(userId);
  }

  @Transactional
  public void unsuspendUser(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new BusinessException(ErrorStatus.USER_NOT_FOUND));
    if (user.getStatus() == UserStatus.WITHDRAWN) {
      throw new BusinessException(ErrorStatus.USER_NOT_FOUND);
    }
    user.unsuspend();
  }

  @Transactional
  public void forceWithdrawUser(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new BusinessException(ErrorStatus.USER_NOT_FOUND));
    if (user.getStatus() == UserStatus.WITHDRAWN) {
      throw new BusinessException(ErrorStatus.USER_NOT_FOUND);
    }
    user.withdraw(null);
    refreshTokenRepository.deleteById(userId);
    fcmTokenService.deleteAllByUserId(userId);
  }

  private static final Set<GroupBuyPostStatus> UNDELETABLE_STATUSES = Set.of(
      GroupBuyPostStatus.CLOSED,
      GroupBuyPostStatus.PAYMENT_DONE,
      GroupBuyPostStatus.SHIPPING
  );

  @Transactional
  public void deletePost(Long postId) {
    GroupBuyPost post = groupBuyRepository.findById(postId)
        .orElseThrow(() -> new BusinessException(ErrorStatus.POST_NOT_FOUND));
    if (UNDELETABLE_STATUSES.contains(post.getStatus())) {
      throw new BusinessException(ErrorStatus.POST_IN_PROGRESS);
    }
    if (orderRepository.existsByGroupBuyPost_Id(postId)) {
      throw new BusinessException(ErrorStatus.POST_HAS_ORDERS);
    }
    try {
      groupBuyRepository.delete(post);
      groupBuyRepository.flush();
    } catch (DataIntegrityViolationException e) {
      throw new BusinessException(ErrorStatus.POST_HAS_ORDERS);
    }
  }
}
