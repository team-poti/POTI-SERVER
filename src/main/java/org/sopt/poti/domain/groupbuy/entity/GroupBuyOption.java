package org.sopt.poti.domain.groupbuy.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.poti.domain.artist.entity.Member;

@Getter
@Entity
@Table(name = "group_buy_options")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupBuyOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "option_name", nullable = false, length = 100)
    private String optionName; // 멤버 명 (ex 레이, 유진)

    @Column(nullable = false)
    private int price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_buy_post_id", nullable = false)
    private GroupBuyPost groupBuyPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public static GroupBuyOption create(Long id, String optionName, int price, GroupBuyPost post, Member member) {
        GroupBuyOption opt = new GroupBuyOption();
        opt.id = id;
        opt.optionName = optionName;
        opt.price = price;
        opt.groupBuyPost = post;
        opt.member = member;
        return opt;
    }
}