package org.sopt.poti.domain.admin.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.poti.domain.auth.repository.RefreshTokenRepository;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyPost;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyPostStatus;
import org.sopt.poti.domain.groupbuy.repository.GroupBuyRepository;
import org.sopt.poti.domain.order.repository.OrderRepository;
import org.sopt.poti.domain.user.entity.User;
import org.sopt.poti.domain.user.entity.UserStatus;
import org.sopt.poti.domain.user.repository.UserRepository;
import org.sopt.poti.global.error.BusinessException;
import org.sopt.poti.global.error.ErrorStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    user.suspend();
    refreshTokenRepository.deleteById(userId);
  }

  @Transactional
  public void unsuspendUser(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new BusinessException(ErrorStatus.USER_NOT_FOUND));
    user.unsuspend();
  }

  @Transactional
  public void forceWithdrawUser(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new BusinessException(ErrorStatus.USER_NOT_FOUND));
    user.withdraw(null);
    refreshTokenRepository.deleteById(userId);
  }

  @Transactional
  public void deletePost(Long postId) {
    GroupBuyPost post = groupBuyRepository.findById(postId)
        .orElseThrow(() -> new BusinessException(ErrorStatus.POST_NOT_FOUND));
    groupBuyRepository.delete(post);
  }
}
