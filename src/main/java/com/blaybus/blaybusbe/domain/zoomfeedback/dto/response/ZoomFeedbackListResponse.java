package com.blaybus.blaybusbe.domain.zoomfeedback.dto.response;

import com.blaybus.blaybusbe.domain.zoomfeedback.entity.ZoomFeedback;

import java.time.LocalDate;

public record ZoomFeedbackListResponse(
        Long id,
        String title,
        LocalDate meetingDate,
        String menteeName // 멘티 이름
) {
    public static ZoomFeedbackListResponse from(ZoomFeedback feedback) {
        return new ZoomFeedbackListResponse(
                feedback.getId(),
                feedback.getTitle(),
                feedback.getMeetingDate(),
                feedback.getMenteeInfo().getMentee().getName()
        );
    }
}