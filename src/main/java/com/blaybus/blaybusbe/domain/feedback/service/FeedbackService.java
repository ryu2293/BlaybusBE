package com.blaybus.blaybusbe.domain.feedback.service;

import com.blaybus.blaybusbe.domain.comment.repository.AnswerRepository;
import com.blaybus.blaybusbe.domain.feedback.dto.request.UpdateFeedbackRequest;
import com.blaybus.blaybusbe.domain.feedback.dto.response.FeedbackResponse;
import com.blaybus.blaybusbe.domain.feedback.entity.TaskFeedback;
import com.blaybus.blaybusbe.domain.feedback.repository.TaskFeedbackRepository;
import com.blaybus.blaybusbe.domain.mentoring.repository.MenteeInfoRepository;
import com.blaybus.blaybusbe.domain.submission.entity.SubmissionImage;
import com.blaybus.blaybusbe.domain.submission.repository.SubmissionImageRepository;
import com.blaybus.blaybusbe.domain.user.entity.User;
import com.blaybus.blaybusbe.domain.user.enums.Role;
import com.blaybus.blaybusbe.domain.user.repository.UserRepository;
import com.blaybus.blaybusbe.global.exception.CustomException;
import com.blaybus.blaybusbe.global.exception.error.ErrorCode;
import com.blaybus.blaybusbe.domain.notification.event.NotificationEvent;
import com.blaybus.blaybusbe.domain.notification.enums.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        // 멘티에게 피드백 알림 발행
        eventPublisher.publishEvent(new NotificationEvent(
                NotificationType.FEEDBACK,
                menteeId,
                String.format("%s 멘토님이 피드백을 작성했습니다.", mentor.getName())
        ));

        return FeedbackResponse.from(feedback, answerRepository.countByFeedbackId(feedback.getId()));
    }

    public List<FeedbackResponse> getFeedbacksByImageId(Long imageId) {
        // 이미지 존재 확인
        if (!imageRepository.existsById(imageId)) {
            throw new CustomException(ErrorCode.IMAGE_NOT_FOUND);
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
}
