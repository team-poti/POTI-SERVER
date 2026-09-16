package org.sopt.poti.domain.artist.dto.response;

import java.util.List;

public record MemberListResponse(
    List<MemberResponse> members
) {

  public record MemberResponse(
      Long memberId,
      String name
  ) {

  }
}
