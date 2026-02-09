package com.blaybus.blaybusbe.domain.zoomfeedback.dto.response;

import com.blaybus.blaybusbe.domain.zoomfeedback.entity.ZoomFeedback;

import java.time.LocalDate;

public record ZoomFeedbackResponse(
        Long id,
        String title,
        String memo,
        String koreanFeedback,
        String mathFeedback,
        String englishFeedback,
        String operateFeedback,
        LocalDate meetingDate,
        String menteeName
) {
    public static ZoomFeedbackResponse from(ZoomFeedback feedback) {
        return new ZoomFeedbackResponse(
                feedback.getId(),
                feedback.getTitle(),
                feedback.getMemo(),
                feedback.getKoreanFeedback(),
                feedback.getMathFeedback(),
                feedback.getEnglishFeedback(),
                feedback.getOperateFeedback(),
                feedback.getMeetingDate(),
                feedback.getMenteeInfo().getMentee().getName()
        );
    }
}