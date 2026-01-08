package org.sopt.poti.domain.groupbuy.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "item_images")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_buy_post_id", nullable = false)
    private GroupBuyPost groupBuyPost;

    public static ItemImage create(Long id, String imageUrl, Integer sortOrder, GroupBuyPost post) {
        ItemImage img = new ItemImage();
        img.id = id;
        img.imageUrl = imageUrl;
        img.sortOrder = sortOrder;
        img.groupBuyPost = post;
        return img;
    }
}