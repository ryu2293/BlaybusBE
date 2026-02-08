package com.blaybus.blaybusbe.domain.plan.dto.response;

import com.blaybus.blaybusbe.global.common.util.TimeUtils;
import com.blaybus.blaybusbe.domain.plan.entity.DailyPlan;
import com.blaybus.blaybusbe.domain.plan.dto.response.DailyPlanTaskResponse;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record PlanResponse(
        Long id,
        LocalDate planDate,
        Long totalStudyTime,
        String totalStudyTimeFormatted,
        String dailyMemo,
        String mentorFeedback,
        Long menteeId,
        List<DailyPlanTaskResponse> tasks,
        LocalDateTime createdAt
) {

    public static PlanResponse from(DailyPlan plan, List<DailyPlanTaskResponse> dailyPlanTasks) {
        return PlanResponse.builder()
                .id(plan.getId())
                .planDate(plan.getPlanDate())
                .totalStudyTime(plan.getTotalStudyTime())
                .totalStudyTimeFormatted(TimeUtils.formatSecondsToHHMMSS(plan.getTotalStudyTime()))
                .dailyMemo(plan.getDailyMemo())
                .mentorFeedback(plan.getMentorFeedback())
                .menteeId(plan.getMentee().getId())
                .tasks(dailyPlanTasks)
                .createdAt(plan.getCreatedAt())
                .build();
    }
}
