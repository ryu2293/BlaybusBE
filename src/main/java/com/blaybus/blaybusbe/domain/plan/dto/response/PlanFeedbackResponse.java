package com.blaybus.blaybusbe.domain.plan.dto.response;

import com.blaybus.blaybusbe.domain.plan.entity.DailyPlan;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PlanFeedbackResponse(
        Long planId,
        String content,
        String mentorName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static PlanFeedbackResponse from(DailyPlan plan, String mentorName) {
        return PlanFeedbackResponse.builder()
                .planId(plan.getId())
                .content(plan.getMentorFeedback())
                .mentorName(mentorName)
                .createdAt(plan.getCreatedAt())
                .updatedAt(plan.getUpdatedAt())
                .build();
    }
}
