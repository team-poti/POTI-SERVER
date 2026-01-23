package org.sopt.poti.domain.groupbuy.repository;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyPost;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyPostStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupBuyRepository extends JpaRepository<GroupBuyPost, Long>,
    GroupBuyRepositoryCustom {

  int countByLeader_Id(Long userId);

  int countByLeader_IdAndStatusIn(Long userId, List<GroupBuyPostStatus> statuses);

  @Query("SELECT g FROM GroupBuyPost g JOIN FETCH g.leader JOIN FETCH g.artist WHERE g.id = :id")
  Optional<GroupBuyPost> findByIdWithUserAndArtist(@Param("id") Long id);

  void deleteByLeaderId(Long leaderId);

  List<GroupBuyPost> findAllByLeaderId(Long leaderId);

  List<GroupBuyPost> findByLeader_IdAndStatusInOrderByCreatedAtDesc(Long leaderId,
      List<GroupBuyPostStatus> statuses);

  boolean existsByOrderNumber(String orderNumber);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select p from GroupBuyPost p where p.id = :postId")
  java.util.Optional<GroupBuyPost> findByIdWithLock(@Param("postId") Long postId);
}
