package com.blaybus.blaybusbe.domain.zoomfeedback.dto.request;

import com.blaybus.blaybusbe.domain.mentoring.entity.MenteeInfo;
import com.blaybus.blaybusbe.domain.zoomfeedback.entity.ZoomFeedback;

import java.time.LocalDate;

public record CreateZoomFeedbackRequest(
        String title,
        String memo,
        String koreanFeedback,
        String mathFeedback,
        String englishFeedback,
        String operateFeedback,
        LocalDate meetingDate
) {

    static public ZoomFeedback dtoToEntity(CreateZoomFeedbackRequest request, MenteeInfo menteeInfo) {
        return ZoomFeedback.builder()
                .title(request.title())
                .memo(request.memo())
                .koreanFeedback(request.koreanFeedback())
                .mathFeedback(request.mathFeedback())
                .englishFeedback(request.englishFeedback())
                .operateFeedback(request.operateFeedback())
                .meetingDate(request.meetingDate())
                .menteeInfo(menteeInfo)
                .build();
    }
}
