package com.blaybus.blaybusbe.domain.zoomfeedback.service;

import com.blaybus.blaybusbe.domain.mentoring.entity.MenteeInfo;
import com.blaybus.blaybusbe.domain.mentoring.repository.MenteeInfoRepository;
import com.blaybus.blaybusbe.domain.zoomfeedback.dto.request.CreateZoomFeedbackRequest;
import com.blaybus.blaybusbe.domain.zoomfeedback.dto.response.ZoomFeedbackListResponse;
import com.blaybus.blaybusbe.domain.zoomfeedback.dto.response.ZoomFeedbackResponse;
import com.blaybus.blaybusbe.domain.zoomfeedback.entity.ZoomFeedback;
import com.blaybus.blaybusbe.domain.zoomfeedback.repository.ZoomFeedbackRepository;
import com.blaybus.blaybusbe.global.exception.CustomException;
import com.blaybus.blaybusbe.global.exception.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ZoomFeedbackService {

    private final ZoomFeedbackRepository zoomFeedbackRepository;
    private final MenteeInfoRepository menteeInfoRepository;

    /**
     * 멘토가 줌 피드백을 작성합니다.
     *
     * @param mentorId 멘토 id
     * @param menteeId 멘토 id
     * @param request
     */
    public Long createZoomFeedback(Long mentorId, Long menteeId, CreateZoomFeedbackRequest request) {
        MenteeInfo menteeInfo = menteeInfoRepository.findByMentorIdAndMenteeId(mentorId, menteeId)
                .orElseThrow(() -> new CustomException(ErrorCode.MENTEE_INFO_NOT_FOUND));

        ZoomFeedback zoomFeedback = CreateZoomFeedbackRequest.dtoToEntity(request, menteeInfo);

        return zoomFeedbackRepository.save(zoomFeedback).getId();
    }

    /**
     * 피드백 상세 조회
     *
     * @param userId 유저 id
     * @param feedbackId 피드백 id
     * @return
     */
    @Transactional(readOnly = true)
    public ZoomFeedbackResponse getZoomFeedback(Long userId, Long feedbackId) {
        ZoomFeedback feedback = zoomFeedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new CustomException(ErrorCode.FEEDBACK_NOT_FOUND));

        Long menteeId = feedback.getMenteeInfo().getMentee().getId();

        // 해당 권한이 없다면 접근 차단
        boolean isOwner = menteeId.equals(userId);
        boolean isAssignedMentor = menteeInfoRepository.existsByMentorIdAndMenteeId(userId, menteeId);

        if (!isOwner && !isAssignedMentor) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        return ZoomFeedbackResponse.from(feedback);
    }

    /**
     * 줌 피드백 목록 조회
     *
     * @param mentorId 멘토 id
     * @param menteeId 멘티 id
     * @param pageable 페이지네이션
     */
    @Transactional(readOnly = true)
    public Page<ZoomFeedbackListResponse> getZoomFeedbackList(Long mentorId, Long menteeId, Pageable pageable) {
        return zoomFeedbackRepository.findByMentorIdAndMenteeId(mentorId, menteeId, pageable)
                .map(response -> ZoomFeedbackListResponse.from(response));
    }

    /**
     * 작성한 줌 피드백 삭제합니다.
     *
     * @param mentorId 멘토 id
     * @param feedbackId 피드백 id
     */
    public void deleteZoomFeedback(Long mentorId, Long feedbackId) {
        ZoomFeedback feedback = zoomFeedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new CustomException(ErrorCode.FEEDBACK_NOT_FOUND));

        if (!feedback.getMenteeInfo().getMentor().getId().equals(mentorId)) {
            throw new CustomException(ErrorCode.FEEDBACK_NOT_OWNER);
        }

        zoomFeedbackRepository.delete(feedback);
    }

    /**
     * 줌 피드백 수정합니다.
     *
     * @param mentorId 멘토 id
     * @param feedbackId 피드백 id
     */
    public ZoomFeedbackResponse updateZoomFeedback(Long mentorId, Long feedbackId, CreateZoomFeedbackRequest request) {
        ZoomFeedback feedback = zoomFeedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new CustomException(ErrorCode.FEEDBACK_NOT_FOUND));

        if (!feedback.getMenteeInfo().getMentor().getId().equals(mentorId)) {
            throw new CustomException(ErrorCode.FEEDBACK_NOT_OWNER);
        }

        feedback.update(
                request.title(),
                request.memo(),
                request.koreanFeedback(),
                request.mathFeedback(),
                request.englishFeedback(),
                request.operateFeedback(),
                request.meetingDate()
        );

        return ZoomFeedbackResponse.from(feedback);
    }
}
