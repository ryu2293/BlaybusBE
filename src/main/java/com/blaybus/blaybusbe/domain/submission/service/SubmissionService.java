package com.blaybus.blaybusbe.domain.submission.service;

import com.blaybus.blaybusbe.domain.mentoring.repository.MenteeInfoRepository;
import com.blaybus.blaybusbe.domain.notification.event.NotificationEvent;
import com.blaybus.blaybusbe.domain.notification.enums.NotificationType;
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

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubmissionService {

    private final TaskSubmissionRepository submissionRepository;
    private final TaskRepository taskRepository;
    private final MenteeInfoRepository menteeInfoRepository;
    private final ApplicationEventPublisher eventPublisher;


    /**
     * 멘티가 과제를 제출합니다.
     *
     * @param userId 유저 id
     * @param taskId 과제 id
     * @param uploadedUrls 업로드한 이미지 url
     * @param menteeComment 제출 내용
     * @return
     */
    @Transactional
    public SubmissionResponse createSubmission(Long userId, Long taskId, List<String> uploadedUrls, String menteeComment) {
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
                .menteeComment(menteeComment)
                .task(task)
                .build();

        // 이미지 추가
        for (String fileUrl : uploadedUrls) {
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
        Long mentorId = menteeInfoRepository.findByMenteeId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.MENTEE_INFO_NOT_FOUND))
                .getMentor().getId();
        String menteeName = task.getMentee().getName();
        eventPublisher.publishEvent(new NotificationEvent(
                NotificationType.SUBMISSION,
                mentorId,
                String.format("%s 학생이 과제를 제출했습니다: %s", menteeName, task.getTitle())
        ));

        return SubmissionResponse.from(submission);
    }

    /**
     * 제출한 과제를 조회합니다.
     *
     * @param userId 유저 id
     * @param taskId 과제 id
     * @return
     */
    public SubmissionResponse getSubmission(Long userId, Long taskId) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new CustomException(ErrorCode.TASK_NOT_FOUND));

        Long menteeId = task.getMentee().getId();

        boolean isOwner = menteeId.equals(userId);
        boolean isAssignedMentor = menteeInfoRepository.existsByMentorIdAndMenteeId(userId, menteeId);

        // 관계 없는 사용자 접근 시 차단
        if (!isOwner && !isAssignedMentor) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

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
