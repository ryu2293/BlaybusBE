package com.blaybus.blaybusbe.domain.task.dto.response;

import com.blaybus.blaybusbe.domain.task.entity.Task;
import com.blaybus.blaybusbe.domain.task.enums.*;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record TaskResponse(
        Long id,
        Subject subject,
        String title,
        TaskStatus status,
        Integer actualStudyTime,
        LocalDate taskDate,
        Boolean isFixed,
        Boolean isMentorChecked,
        Boolean isMandatory,
        String goal,
        String description,
        Integer weekNumber,
        DayOfWeekEnum dayOfWeek,
        String recurringGroupId,
        TimerStatus timerStatus,
        Long contentId,
        Long weaknessId,
        Long dailyPlanId,
        Long menteeId,
        Boolean submitted,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static TaskResponse from(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .subject(task.getSubject())
                .title(task.getTitle())
                .status(task.getStatus())
                .actualStudyTime(task.getActualStudyTime())
                .taskDate(task.getTaskDate())
                .isFixed(task.getIsFixed())
                .isMentorChecked(task.getIsMentorChecked())
                .isMandatory(task.getIsMandatory())
                .goal(task.getGoal())
                .description(task.getDescription())
                .weekNumber(task.getWeekNumber())
                .dayOfWeek(task.getDayOfWeek())
                .recurringGroupId(task.getRecurringGroupId())
                .timerStatus(task.getTimerStatus())
                .contentId(task.getContentId())
                .weaknessId(task.getWeaknessId())
                .dailyPlanId(task.getDailyPlan().getId())
                .menteeId(task.getMentee().getId())
                .submitted(false) // Submission 도메인 미구현 → 항상 false
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}
