package com.blaybus.blaybusbe.domain.plan.dto.response;

import com.blaybus.blaybusbe.domain.plan.entity.DailyPlan;
import com.blaybus.blaybusbe.domain.task.dto.response.TaskResponse;
import com.blaybus.blaybusbe.domain.task.entity.Task;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record PlanResponse(
        Long id,
        LocalDate planDate,
        Integer totalStudyTime,
        String dailyMemo,
        String mentorFeedback,
        Long menteeId,
        List<TaskResponse> tasks,
        LocalDateTime createdAt
) {

    public static PlanResponse from(DailyPlan plan, List<Task> taskList) {
        return PlanResponse.builder()
                .id(plan.getId())
                .planDate(plan.getPlanDate())
                .totalStudyTime(plan.getTotalStudyTime())
                .dailyMemo(plan.getDailyMemo())
                .mentorFeedback(plan.getMentorFeedback())
                .menteeId(plan.getMentee().getId())
                .tasks(taskList.stream().map(TaskResponse::from).toList())
                .createdAt(plan.getCreatedAt())
                .build();
    }
}
