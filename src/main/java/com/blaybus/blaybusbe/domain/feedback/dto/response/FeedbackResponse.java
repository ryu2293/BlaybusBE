package com.blaybus.blaybusbe.domain.feedback.dto.response;

import com.blaybus.blaybusbe.domain.feedback.entity.TaskFeedback;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class FeedbackResponse {

    private Long id;
    private String content;
    private String imageUrl;
    private Float xPos;
    private Float yPos;
    private Long mentorId;
    private String mentorName;
    private Integer commentCount;
    private LocalDateTime createdAt;

    public static FeedbackResponse from(TaskFeedback feedback, int commentCount) {
        return FeedbackResponse.builder()
                .id(feedback.getId())
                .content(feedback.getContent())
                .imageUrl(feedback.getImageUrl())
                .xPos(feedback.getXPos())
                .yPos(feedback.getYPos())
                .mentorId(feedback.getMentor().getId())
                .mentorName(feedback.getMentor().getName())
                .commentCount(commentCount)
                .createdAt(feedback.getCreatedAt())
                .build();
    }
}
