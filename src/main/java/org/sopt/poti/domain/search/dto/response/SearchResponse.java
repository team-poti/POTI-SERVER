package org.sopt.poti.domain.search.dto.response;

import java.util.List;
import org.sopt.poti.domain.feed.dto.response.FeedGroupItem;
import org.springframework.data.domain.Slice;

public record SearchResponse(
    List<FeedGroupItem> content,
    boolean hasNext
) {

  public static SearchResponse from(Slice<FeedGroupItem> slice) {
    return new SearchResponse(slice.getContent(), slice.hasNext());
  }
}
