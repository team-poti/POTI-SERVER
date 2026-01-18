package org.sopt.poti.domain.participation.dto.response;

public record ParticipationListResponse(
    Long participationId,
    Long groupBuyId,
    String artistName,
    String productName,
    String thumbnailUrl,
    String status
) {

}