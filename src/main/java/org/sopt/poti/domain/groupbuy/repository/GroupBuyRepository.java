package org.sopt.poti.domain.groupbuy.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyPost;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyPostStatus;
import org.springframework.data.jpa.repository.JpaRepository;
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

  boolean existsByArtist_Id(Long artistId);

  long countByArtist_Id(Long artistId);

  Page<GroupBuyPost> findByStatus(GroupBuyPostStatus status, Pageable pageable);

  @Query("SELECT p FROM GroupBuyPost p JOIN FETCH p.leader WHERE p.status = :status AND p.recruitDeadline = :date")
  List<GroupBuyPost> findByStatusAndRecruitDeadline(@Param("status") GroupBuyPostStatus status, @Param("date") LocalDate date);

  @Query("SELECT p FROM GroupBuyPost p JOIN FETCH p.leader WHERE p.status = :status AND p.closedAt BETWEEN :start AND :end")
  List<GroupBuyPost> findByStatusAndClosedAtBetween(@Param("status") GroupBuyPostStatus status, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
