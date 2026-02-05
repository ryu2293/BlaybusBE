package com.blaybus.blaybusbe.domain.plan.dto.response;

import com.blaybus.blaybusbe.domain.plan.entity.DailyPlan;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record PlanResponse(
        Long id,
        LocalDate planDate,
        Integer totalStudyTime,
        String dailyMemo,
        String mentorFeedback,
        Long menteeId,
        LocalDateTime createdAt
) {

    public static PlanResponse from(DailyPlan plan) {
        return PlanResponse.builder()
                .id(plan.getId())
                .planDate(plan.getPlanDate())
                .totalStudyTime(plan.getTotalStudyTime())
                .dailyMemo(plan.getDailyMemo())
                .mentorFeedback(plan.getMentorFeedback())
                .menteeId(plan.getMentee().getId())
                .createdAt(plan.getCreatedAt())
                .build();
    }
}
