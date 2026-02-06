package com.blaybus.blaybusbe.domain.comment.service;

import com.blaybus.blaybusbe.domain.comment.dto.request.CreateCommentRequest;
import com.blaybus.blaybusbe.domain.comment.dto.request.UpdateCommentRequest;
import com.blaybus.blaybusbe.domain.comment.dto.response.CommentResponse;
import com.blaybus.blaybusbe.domain.comment.entity.Answer;
import com.blaybus.blaybusbe.domain.comment.repository.AnswerRepository;
import com.blaybus.blaybusbe.domain.feedback.entity.TaskFeedback;
import com.blaybus.blaybusbe.domain.feedback.repository.TaskFeedbackRepository;
import com.blaybus.blaybusbe.domain.user.entity.User;
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
public class CommentService {

    private final AnswerRepository answerRepository;
    private final TaskFeedbackRepository feedbackRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public CommentResponse createComment(Long userId, Long feedbackId, CreateCommentRequest request) {
        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 피드백 조회
        TaskFeedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new CustomException(ErrorCode.FEEDBACK_NOT_FOUND));

        // 댓글 생성
        Answer answer = Answer.builder()
                .comment(request.getComment())
                .user(user)
                .feedback(feedback)
                .build();

        answerRepository.save(answer);

        // 상대방에게 댓글 알림 발행
        Long mentorId = feedback.getMentor().getId();
        Long menteeId = feedback.getTask().getMentee().getId();
        Long recipientId = userId.equals(mentorId) ? menteeId : mentorId;
        eventPublisher.publishEvent(new NotificationEvent(
                NotificationType.COMMENT,
                recipientId,
                String.format("%s님이 댓글을 작성했습니다.", user.getName())
        ));

        return CommentResponse.from(answer);
    }

    public List<CommentResponse> getComments(Long feedbackId) {
        // 피드백 존재 확인
        if (!feedbackRepository.existsById(feedbackId)) {
            throw new CustomException(ErrorCode.FEEDBACK_NOT_FOUND);
        }

        List<Answer> answers = answerRepository.findByFeedbackIdOrderByCreatedAtAsc(feedbackId);

        return answers.stream()
                .map(CommentResponse::from)
                .toList();
    }

    @Transactional
    public CommentResponse updateComment(Long userId, Long commentId, UpdateCommentRequest request) {
        Answer answer = answerRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        // 본인 댓글인지 확인
        if (!answer.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.COMMENT_NOT_OWNER);
        }

        answer.update(request.getComment());

        return CommentResponse.from(answer);
    }

    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        Answer answer = answerRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        // 본인 댓글인지 확인
        if (!answer.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.COMMENT_NOT_OWNER);
        }

        answerRepository.delete(answer);
    }
}
