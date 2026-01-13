package org.sopt.poti.domain.feed.dto.request;

import lombok.Builder;
import org.springframework.data.domain.Pageable;

@Builder
public record FeedSearchCondition(
    Long artistId,
    String sort, // LATEST, HOT
    Pageable pageable
) {
}
