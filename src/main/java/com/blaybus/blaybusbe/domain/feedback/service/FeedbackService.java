package com.blaybus.blaybusbe.domain.feedback.service;

import com.blaybus.blaybusbe.domain.comment.repository.AnswerRepository;
import com.blaybus.blaybusbe.domain.feedback.dto.request.UpdateFeedbackRequest;
import com.blaybus.blaybusbe.domain.feedback.dto.response.FeedbackListResponse;
import com.blaybus.blaybusbe.domain.feedback.dto.response.FeedbackResponse;
import com.blaybus.blaybusbe.domain.feedback.entity.TaskFeedback;
import com.blaybus.blaybusbe.domain.feedback.repository.TaskFeedbackRepository;
import com.blaybus.blaybusbe.domain.mentoring.repository.MenteeInfoRepository;
import com.blaybus.blaybusbe.domain.submission.entity.SubmissionImage;
import com.blaybus.blaybusbe.domain.submission.repository.SubmissionImageRepository;
import com.blaybus.blaybusbe.domain.task.enums.Subject;
import com.blaybus.blaybusbe.domain.user.entity.User;
import com.blaybus.blaybusbe.domain.user.enums.Role;
import com.blaybus.blaybusbe.domain.user.repository.UserRepository;
import com.blaybus.blaybusbe.global.exception.CustomException;
import com.blaybus.blaybusbe.global.exception.error.ErrorCode;
import com.blaybus.blaybusbe.domain.notification.event.NotificationEvent;
import com.blaybus.blaybusbe.domain.notification.enums.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedbackService {

    private final TaskFeedbackRepository feedbackRepository;
    private final SubmissionImageRepository imageRepository;
    private final UserRepository userRepository;
    private final AnswerRepository answerRepository;
    private final MenteeInfoRepository menteeInfoRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 이미지에 위치를 찍어서 피드백합니다.
     *
     * @param mentorId 멘토 Id
     * @param imageId 이미지 id
     * @param imageUrl 업로드한 이미지 url
     * @param content 피드백 내용
     * @param xPos
     * @param yPos
     */
    @Transactional
    public FeedbackResponse createFeedback(Long mentorId, Long imageId, String imageUrl, String content, Float xPos, Float yPos) {
        // 멘토 조회
        User mentor = userRepository.findById(mentorId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 멘토 권한 확인
        if (mentor.getRole() != Role.MENTOR) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        // 이미지 조회
        SubmissionImage image = imageRepository.findById(imageId)
                .orElseThrow(() -> new CustomException(ErrorCode.IMAGE_NOT_FOUND));

        // 멘토-멘티 매핑 확인
        Long menteeId = image.getSubmission().getTask().getMentee().getId();
        if (!menteeInfoRepository.existsByMentorIdAndMenteeId(mentorId, menteeId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        // 피드백 생성
        TaskFeedback feedback = TaskFeedback.builder()
                .content(content)
                .imageUrl(imageUrl)
                .xPos(xPos)
                .yPos(yPos)
                .task(image.getSubmission().getTask())
                .mentor(mentor)
                .image(image)
                .build();

        feedbackRepository.save(feedback);

        // 피드백 작성 시 멘토 확인 체크 자동 처리
        image.getSubmission().getTask().setIsMentorChecked(true);

        // 멘티에게 피드백 알림 발행
        eventPublisher.publishEvent(new NotificationEvent(
                NotificationType.FEEDBACK,
                menteeId,
                String.format("%s 멘토님이 피드백을 작성했습니다.", mentor.getName())
        ));

        return FeedbackResponse.from(feedback, answerRepository.countByFeedbackId(feedback.getId()));
    }

    public List<FeedbackResponse> getFeedbacksByImageId(Long userId, Long imageId) {
        SubmissionImage image = imageRepository.findById(imageId)
                .orElseThrow(() -> new CustomException(ErrorCode.IMAGE_NOT_FOUND));

        // 멘티 본인 또는 담당 멘토만 조회 가능
        Long menteeId = image.getSubmission().getTask().getMentee().getId();
        boolean isOwner = menteeId.equals(userId);
        boolean isAssignedMentor = menteeInfoRepository.existsByMentorIdAndMenteeId(userId, menteeId);
        if (!isOwner && !isAssignedMentor) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        List<TaskFeedback> feedbacks = feedbackRepository.findByImageIdWithMentor(imageId);

        return feedbacks.stream()
                .map(feedback -> FeedbackResponse.from(feedback, answerRepository.countByFeedbackId(feedback.getId())))
                .toList();
    }

    @Transactional
    public FeedbackResponse updateFeedback(Long mentorId, Long feedbackId, UpdateFeedbackRequest request) {
        TaskFeedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new CustomException(ErrorCode.FEEDBACK_NOT_FOUND));

        // 본인 피드백인지 확인
        if (!feedback.getMentor().getId().equals(mentorId)) {
            throw new CustomException(ErrorCode.FEEDBACK_NOT_OWNER);
        }

        feedback.update(
                request.getContent(),
                request.getImageUrl(),
                request.getXPos(),
                request.getYPos()
        );

        return FeedbackResponse.from(feedback, answerRepository.countByFeedbackId(feedbackId));
    }

    @Transactional
    public void deleteFeedback(Long mentorId, Long feedbackId) {
        TaskFeedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new CustomException(ErrorCode.FEEDBACK_NOT_FOUND));

        // 본인 피드백인지 확인
        if (!feedback.getMentor().getId().equals(mentorId)) {
            throw new CustomException(ErrorCode.FEEDBACK_NOT_OWNER);
        }

        feedbackRepository.delete(feedback);
    }

    /**
     * 어제자 피드백 목록 조회 (페이징, 멘티 본인만 조회 가능)
     */
    public Page<FeedbackListResponse> getYesterdayFeedbacks(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (user.getRole() != Role.MENTEE) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        LocalDate yesterday = LocalDate.now().minusDays(1);
        Page<TaskFeedback> feedbackPage = feedbackRepository.findYesterdayFeedbacks(userId, yesterday, pageable);

        return feedbackPage.map(f -> FeedbackListResponse.from(f, answerRepository.countByFeedbackId(f.getId())));
    }

    /**
     * 이전 피드백 모아보기 (과목, 년도, 월, 시작일, 종료일 필터 + 페이징)
     * menteeId == userId: 본인(멘티) 피드백 조회
     * menteeId != userId: 멘토-멘티 매핑 검증 후 조회
     * weekNumber는 프론트 표시용으로만 받고, 실제 필터는 startDate/endDate로 적용
     */
    public Page<FeedbackListResponse> getFeedbackHistory(Long userId, Long menteeId, Subject subject,
                                                         Integer year, Integer month,
                                                         LocalDate startDate, LocalDate endDate,
                                                         Pageable pageable) {
        // 본인 조회가 아닌 경우 멘토-멘티 매핑 검증
        if (!menteeId.equals(userId)) {
            if (!menteeInfoRepository.existsByMentorIdAndMenteeId(userId, menteeId)) {
                throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
            }
        }

        Page<TaskFeedback> feedbackPage = feedbackRepository.findFeedbacksWithFilters(
                menteeId, subject, year, month, startDate, endDate, pageable);

        return feedbackPage.map(f -> FeedbackListResponse.from(f, answerRepository.countByFeedbackId(f.getId())));
    }
}
