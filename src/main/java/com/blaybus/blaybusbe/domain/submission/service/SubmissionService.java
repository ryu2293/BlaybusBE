package com.blaybus.blaybusbe.domain.submission.service;

import com.blaybus.blaybusbe.domain.mentoring.repository.MenteeInfoRepository;
import com.blaybus.blaybusbe.domain.notification.event.NotificationEvent;
import com.blaybus.blaybusbe.domain.notification.enums.NotificationType;
import com.blaybus.blaybusbe.domain.submission.dto.request.CreateSubmissionRequest;
import com.blaybus.blaybusbe.domain.submission.dto.response.SubmissionResponse;
import com.blaybus.blaybusbe.domain.submission.entity.SubmissionImage;
import com.blaybus.blaybusbe.domain.submission.entity.TaskSubmission;
import com.blaybus.blaybusbe.domain.submission.repository.TaskSubmissionRepository;
import com.blaybus.blaybusbe.domain.task.entity.Task;
import com.blaybus.blaybusbe.domain.task.enums.TaskStatus;
import com.blaybus.blaybusbe.domain.task.repository.TaskRepository;
import com.blaybus.blaybusbe.global.exception.CustomException;
import com.blaybus.blaybusbe.global.exception.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubmissionService {

    private final TaskSubmissionRepository submissionRepository;
    private final TaskRepository taskRepository;
    private final MenteeInfoRepository menteeInfoRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public SubmissionResponse createSubmission(Long userId, Long taskId, CreateSubmissionRequest request) {
        // Task 조회
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new CustomException(ErrorCode.TASK_NOT_FOUND));

        // 본인 과제인지 확인
        if (!task.getMentee().getId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        // 이미 제출물이 있는지 확인
        if (submissionRepository.existsByTaskId(taskId)) {
            throw new CustomException(ErrorCode.SUBMISSION_ALREADY_EXISTS);
        }

        // 제출물 생성
        TaskSubmission submission = TaskSubmission.builder()
                .menteeComment(request.getMenteeComment())
                .task(task)
                .build();

        // 이미지 추가
        for (String fileUrl : request.getFileUrls()) {
            SubmissionImage image = SubmissionImage.builder()
                    .imageUrl(fileUrl)
                    .build();
            submission.addImage(image);
        }

        submissionRepository.save(submission);

        // Task 상태를 DONE으로 변경
        task.setStatus(TaskStatus.DONE);
        taskRepository.save(task);

        // 멘토에게 과제 제출 알림 발행
        menteeInfoRepository.findByMenteeId(userId).ifPresent(menteeInfo -> {
            Long mentorId = menteeInfo.getMentor().getId();
            String menteeName = task.getMentee().getName();
            eventPublisher.publishEvent(new NotificationEvent(
                    NotificationType.SUBMISSION,
                    mentorId,
                    String.format("%s 학생이 과제를 제출했습니다: %s", menteeName, task.getTitle())
            ));
        });

        return SubmissionResponse.from(submission);
    }

    public SubmissionResponse getSubmission(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new CustomException(ErrorCode.TASK_NOT_FOUND));

        TaskSubmission submission = submissionRepository.findByTaskId(taskId)
                .orElseThrow(() -> new CustomException(ErrorCode.SUBMISSION_NOT_FOUND));

        return SubmissionResponse.from(submission);
    }

    @Transactional
    public void deleteSubmission(Long userId, Long submissionId) {
        TaskSubmission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new CustomException(ErrorCode.SUBMISSION_NOT_FOUND));

        // 본인 제출물인지 확인
        if (!submission.getTask().getMentee().getId().equals(userId)) {
            throw new CustomException(ErrorCode.SUBMISSION_NOT_OWNER);
        }

        // Task 상태를 TODO로 되돌림
        Task task = submission.getTask();
        task.setStatus(TaskStatus.TODO);
        taskRepository.save(task);

        submissionRepository.delete(submission);
    }
}
