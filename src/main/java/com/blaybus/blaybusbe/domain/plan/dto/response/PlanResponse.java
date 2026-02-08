package com.blaybus.blaybusbe.domain.plan.dto.response;

import com.blaybus.blaybusbe.global.common.util.TimeUtils;
import com.blaybus.blaybusbe.domain.plan.entity.DailyPlan;
import com.blaybus.blaybusbe.domain.task.dto.response.TaskResponse;
import com.blaybus.blaybusbe.domain.task.entity.Task;
import com.blaybus.blaybusbe.domain.task.enums.Subject;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Builder
public record PlanResponse(
        Long id,
        LocalDate planDate,
        Long totalStudyTime,
        String totalStudyTimeFormatted,
        Map<String, Long> studyTimeSummary,
        String dailyMemo,
        String mentorFeedback,
        Long menteeId,
        List<TaskResponse> tasks,
        LocalDateTime createdAt
) {

    public static PlanResponse from(DailyPlan plan, List<Task> taskList) {
        Map<String, Long> summary = new HashMap<>();
        for (Subject s : Subject.values()) {
            summary.put(s.name(), taskList.stream()
                    .filter(t -> t.getSubject() == s)
                    .mapToLong(Task::getActualStudyTime)
                    .sum());
        }

        return PlanResponse.builder()
                .id(plan.getId())
                .planDate(plan.getPlanDate())
                .totalStudyTime(plan.getTotalStudyTime())
                .totalStudyTimeFormatted(TimeUtils.formatSecondsToHHMMSS(plan.getTotalStudyTime()))
                .studyTimeSummary(summary)
                .dailyMemo(plan.getDailyMemo())
                .mentorFeedback(plan.getMentorFeedback())
                .menteeId(plan.getMentee().getId())
                .tasks(taskList.stream().map(TaskResponse::from).toList())
                .createdAt(plan.getCreatedAt())
                .build();
    }
}
