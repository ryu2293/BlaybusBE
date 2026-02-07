package com.blaybus.blaybusbe.domain.feedback.dto.response;

import com.blaybus.blaybusbe.domain.feedback.entity.TaskFeedback;
import com.blaybus.blaybusbe.domain.task.enums.Subject;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record FeedbackListResponse(
        Long feedbackId,
        String content,
        String imageUrl,
        Float xPos,
        Float yPos,
        String mentorName,
        Integer commentCount,
        LocalDateTime createdAt,
        Long taskId,
        String taskTitle,
        Subject subject,
        LocalDate taskDate,
        Integer weekNumber
) {
    public static FeedbackListResponse from(TaskFeedback feedback, int commentCount) {
        return FeedbackListResponse.builder()
                .feedbackId(feedback.getId())
                .content(feedback.getContent())
                .imageUrl(feedback.getImageUrl())
                .xPos(feedback.getXPos())
                .yPos(feedback.getYPos())
                .mentorName(feedback.getMentor().getName())
                .commentCount(commentCount)
                .createdAt(feedback.getCreatedAt())
                .taskId(feedback.getTask().getId())
                .taskTitle(feedback.getTask().getTitle())
                .subject(feedback.getTask().getSubject())
                .taskDate(feedback.getTask().getTaskDate())
                .weekNumber(feedback.getTask().getWeekNumber())
                .build();
    }
}
