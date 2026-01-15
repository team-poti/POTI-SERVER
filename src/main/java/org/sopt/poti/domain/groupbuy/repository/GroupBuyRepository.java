package org.sopt.poti.domain.groupbuy.repository;

import org.sopt.poti.domain.groupbuy.entity.GroupBuyPost;
import org.sopt.poti.domain.groupbuy.entity.GroupBuyPostStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface GroupBuyRepository extends JpaRepository<GroupBuyPost, Long>, GroupBuyRepositoryCustom {
    int countByLeader_Id(Long userId);

    int countByLeader_IdAndStatusIn(Long userId, List<GroupBuyPostStatus> statuses);

    @Query("SELECT g FROM GroupBuyPost g JOIN FETCH g.leader JOIN FETCH g.artist WHERE g.id = :id")
    Optional<GroupBuyPost> findByIdWithUserAndArtist(@Param("id") Long id);

    void deleteByLeaderId(Long leaderId);

    List<GroupBuyPost> findAllByLeaderId(Long leaderId);

    List<GroupBuyPost> findByLeader_IdAndStatusInOrderByCreatedAtDesc(Long leaderId, List<GroupBuyPostStatus> statuses);
}
