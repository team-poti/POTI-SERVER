package org.sopt.poti.domain.groupbuy.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.poti.domain.artist.entity.Artist;
import org.sopt.poti.domain.user.entity.User;
import org.sopt.poti.global.entity.BaseTimeEntity;

@Getter
@Entity
@Table(name = "group_buy_posts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupBuyPost extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "recruit_deadline")
    private LocalDate recruitDeadline;

    @Column(name = "bank_name", length = 50)
    private String bankName;

    @Column(name = "account_number", length = 50)
    private String accountNumber;

    @Column(name = "account_holder", length = 50)
    private String accountHolder;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private GroupBuyPostStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_user_id", nullable = false)
    private User leader; // 총대

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;

    @Builder
    private GroupBuyPost(
            String title,
            String content,
            LocalDate recruitDeadline,
            String bankName,
            String accountNumber,
            String accountHolder,
            User leader,
            Artist artist
    ) {
        this.title = title;
        this.content = content;
        this.recruitDeadline = recruitDeadline;
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.leader = leader;
        this.artist = artist;
        this.status = GroupBuyPostStatus.RECRUITING;
    }

    public static GroupBuyPost create(
            String title,
            String content,
            LocalDate recruitDeadline,
            String bankName,
            String accountNumber,
            String accountHolder,
            User leader,
            Artist artist
    ) {
        return GroupBuyPost.builder()
                .title(title)
                .content(content)
                .recruitDeadline(recruitDeadline)
                .bankName(bankName)
                .accountNumber(accountNumber)
                .accountHolder(accountHolder)
                .leader(leader)
                .artist(artist)
                .build();
    }
}