package org.sopt.poti.domain.participation.dto.response;

import java.util.List;
import org.sopt.poti.domain.participation.entity.ParticipationStatus;

public record ParticipationSummaryResponse(
    ParticipationStatus currentStatus,
    int inProgressCount,
    int completedCount,
    List<ParticipationListResponse> participations
) {

}
