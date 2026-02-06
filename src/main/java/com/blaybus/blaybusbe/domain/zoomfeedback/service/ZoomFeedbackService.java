package com.blaybus.blaybusbe.domain.zoomfeedback.service;

import com.blaybus.blaybusbe.domain.mentoring.entity.MenteeInfo;
import com.blaybus.blaybusbe.domain.mentoring.repository.MenteeInfoRepository;
import com.blaybus.blaybusbe.domain.zoomfeedback.dto.request.CreateZoomFeedbackRequest;
import com.blaybus.blaybusbe.domain.zoomfeedback.dto.response.ZoomFeedbackResponse;
import com.blaybus.blaybusbe.domain.zoomfeedback.entity.ZoomFeedback;
import com.blaybus.blaybusbe.domain.zoomfeedback.repository.ZoomFeedbackRepository;
import com.blaybus.blaybusbe.global.exception.CustomException;
import com.blaybus.blaybusbe.global.exception.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ZoomFeedbackService {

    private final ZoomFeedbackRepository zoomFeedbackRepository;
    private final MenteeInfoRepository menteeInfoRepository;

    public Long createZoomFeedback(Long mentorId, Long menteeId, CreateZoomFeedbackRequest request) {
        MenteeInfo menteeInfo = menteeInfoRepository.findByMentorIdAndMenteeId(mentorId, menteeId)
                .orElseThrow(() -> new CustomException(ErrorCode.MENTEE_INFO_NOT_FOUND));

        ZoomFeedback zoomFeedback = CreateZoomFeedbackRequest.dtoToEntity(request, menteeInfo);

        return zoomFeedbackRepository.save(zoomFeedback).getId();
    }
}
