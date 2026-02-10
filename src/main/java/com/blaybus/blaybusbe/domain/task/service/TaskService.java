package com.blaybus.blaybusbe.domain.task.service;

import com.blaybus.blaybusbe.domain.mentoring.repository.MenteeInfoRepository;
import com.blaybus.blaybusbe.domain.notification.event.NotificationEvent;
import com.blaybus.blaybusbe.domain.notification.enums.NotificationType;
import com.blaybus.blaybusbe.domain.plan.entity.DailyPlan;
import com.blaybus.blaybusbe.domain.plan.repository.DailyPlanRepository;
import com.blaybus.blaybusbe.domain.studyContent.entitiy.StudyContents;
import com.blaybus.blaybusbe.domain.studyContent.repository.StudyContentRepository;
import com.blaybus.blaybusbe.domain.task.dto.request.CreateMenteeTaskRequest;
import com.blaybus.blaybusbe.domain.task.dto.request.CreateMentorTaskRequest;
import com.blaybus.blaybusbe.domain.task.dto.request.UpdateTaskRequest;
import com.blaybus.blaybusbe.domain.task.dto.response.RecurringTaskResponse;
import com.blaybus.blaybusbe.domain.task.dto.response.TaskLogResponse;
import com.blaybus.blaybusbe.domain.task.dto.response.TaskResponse;
import com.blaybus.blaybusbe.domain.task.dto.response.TimerResponse;
import com.blaybus.blaybusbe.domain.task.dto.response.TimerStopResponse;
import com.blaybus.blaybusbe.domain.task.entity.Task;
import com.blaybus.blaybusbe.domain.task.entity.TaskLog;
import com.blaybus.blaybusbe.domain.task.enums.DayOfWeekEnum;
import com.blaybus.blaybusbe.domain.task.enums.TimerStatus;

import com.blaybus.blaybusbe.domain.task.repository.TaskLogRepository;
import com.blaybus.blaybusbe.domain.task.repository.TaskRepository;
import com.blaybus.blaybusbe.domain.user.entity.User;
import com.blaybus.blaybusbe.domain.user.enums.Role;
import com.blaybus.blaybusbe.domain.user.repository.UserRepository;
import com.blaybus.blaybusbe.domain.weakness.entitiy.Weakness;
import com.blaybus.blaybusbe.domain.weakness.repository.WeaknessRepository;
import com.blaybus.blaybusbe.global.exception.CustomException;
import com.blaybus.blaybusbe.global.exception.error.ErrorCode;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskLogRepository taskLogRepository;
    private final DailyPlanRepository dailyPlanRepository;
    private final UserRepository userRepository;
    private final MenteeInfoRepository menteeInfoRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final StudyContentRepository studyContentRepository;
    private final WeaknessRepository WeaknessRepository;

    /**
     * 멘토 과제 출제 (is_mandatory=true)
     * 주차 + 요일 기반으로 startDate~endDate 범위 내 선택된 요일에 Task 일괄 생성
     * 요일별 학습지(contentId) 매핑
     */
    public RecurringTaskResponse createMentorTask(Long mentorId, Long menteeId, CreateMentorTaskRequest request) {
        // 멘토-멘티 매핑 검증
        if (!menteeInfoRepository.existsByMentorIdAndMenteeId(mentorId, menteeId)) {
            throw new CustomException(ErrorCode.MENTEE_INFO_NOT_FOUND);
        }

        User mentee = userRepository.findById(menteeId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 요일별 contentId 매핑 구성
        java.util.Map<DayOfWeekEnum, Long> dayContentMap = new java.util.HashMap<>();
        if (request.dayContents() != null) {
            for (CreateMentorTaskRequest.DayContentMapping mapping : request.dayContents()) {
                if (!request.daysOfWeek().contains(mapping.day())) {
                    throw new CustomException(ErrorCode.INVALID_DAY_CONTENT_MAPPING);
                }
                dayContentMap.put(mapping.day(), mapping.contentId());
            }
        }

        List<Task> createdTasks = new ArrayList<>();

        LocalDate current = request.startDate();
        while (!current.isAfter(request.endDate())) {
            for (DayOfWeekEnum day : request.daysOfWeek()) {
                if (current.getDayOfWeek() == day.toDayOfWeek()) {
                    DailyPlan dailyPlan = findOrCreateDailyPlan(mentee, current);

                    Task task = Task.builder()
                            .subject(request.subject())
                            .title(request.title())
                            .taskDate(current)
                            .isMandatory(true)
                            .weekNumber(request.weekNumber())
                            .weaknessId(request.weaknessId())
                            .contentId(dayContentMap.get(day))
                            .dailyPlan(dailyPlan)
                            .mentee(mentee)
                            .build();

                    taskRepository.save(task);
                    createdTasks.add(task);
                }
            }
            current = current.plusDays(1);
        }

        // 멘티에게 과제 출제 알림
        User mentor = userRepository.findById(mentorId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        eventPublisher.publishEvent(new NotificationEvent(
                NotificationType.TASK,
                menteeId,
                String.format("%s 멘토님이 새 과제를 출제했습니다: %s", mentor.getName(), request.title()),
                null,
                null,
                null,
                menteeId
        ));

        return RecurringTaskResponse.builder()
                .taskCount(createdTasks.size())
                .tasks(createdTasks.stream().map(TaskResponse::from).toList())
                .build();
    }

    /**
     * 멘티 할 일 추가 (is_mandatory=false)
     */
    public TaskResponse createMenteeTask(Long menteeId, CreateMenteeTaskRequest request) {
        User mentee = userRepository.findById(menteeId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        DailyPlan dailyPlan = findOrCreateDailyPlan(mentee, request.date());

        Task task = Task.builder()
                .subject(request.subject())
                .title(request.title())
                .taskDate(request.date())
                .dailyPlan(dailyPlan)
                .mentee(mentee)
                .build();

        taskRepository.save(task);
        return TaskResponse.from(task);
    }

    /**
     * 과제 상세 조회
     */
    @Transactional(readOnly = true)
    public TaskResponse getTask(Long userId, Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new CustomException(ErrorCode.TASK_NOT_FOUND));

        validateTaskViewPermission(task, userId);

        String fileUrl = null;
        String fileName = null;

        // 일반 학습 자료가 등록된 경우
        if(task.getContentId() != null) {
            StudyContents studyContents = studyContentRepository.findById(task.getContentId()).orElse(null);
            if (studyContents != null) {
                fileName = studyContents.getTitle();
                fileUrl = studyContents.getContentUrl();
            }
        }
        // 멘티의 보완점이 등록된 경우
        else if(task.getWeaknessId() != null) {
            Weakness weakness = WeaknessRepository.findById(task.getWeaknessId()).orElse(null);
            if (weakness != null && weakness.getStudyContent() != null) {
                fileName = weakness.getStudyContent().getTitle();
                fileUrl = weakness.getStudyContent().getContentUrl();
            }
        }

        return TaskResponse.from(task, fileName, fileUrl);
    }

    /**
     * 과제 수정
     * - 필수 과제(is_mandatory=true): 멘토만 수정 가능
     * - 자율 과제(is_mandatory=false): 멘티 본인만 수정 가능
     */
    public TaskResponse updateTask(Long userId, Role role, Long taskId, UpdateTaskRequest request) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new CustomException(ErrorCode.TASK_NOT_FOUND));

        validateTaskModifyPermission(task, userId, role);

        if (request.title() != null) task.setTitle(request.title());
        if (request.subject() != null) task.setSubject(request.subject());
        if (request.status() != null) task.setStatus(request.status());

        return TaskResponse.from(task);
    }

    /**
     * 과제 삭제
     */
    public void deleteTask(Long userId, Role role, Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new CustomException(ErrorCode.TASK_NOT_FOUND));

        validateTaskModifyPermission(task, userId, role);

        taskRepository.delete(task);
    }

    /**
     * 멘토 확인 체크 토글
     */
    public TaskResponse confirmTask(Long mentorId, Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new CustomException(ErrorCode.TASK_NOT_FOUND));

        // 멘토-멘티 매핑 검증
        if (!menteeInfoRepository.existsByMentorIdAndMenteeId(mentorId, task.getMentee().getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        task.setIsMentorChecked(!task.getIsMentorChecked());
        return TaskResponse.from(task);
    }

    /**
     * 타이머 시작
     */
    public TimerResponse startTimer(Long menteeId, Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new CustomException(ErrorCode.TASK_NOT_FOUND));

        if (!task.getMentee().getId().equals(menteeId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        if (task.getTimerStatus() == TimerStatus.RUNNING) {
            throw new CustomException(ErrorCode.TIMER_ALREADY_RUNNING);
        }

        task.setTimerStatus(TimerStatus.RUNNING);
        task.setTimerStartedAt(LocalDateTime.now());

        // 타이머 시작 시 상태를 IN_PROGRESS로 변경
        if (task.getStatus() == com.blaybus.blaybusbe.domain.task.enums.TaskStatus.TODO) {
            task.setStatus(com.blaybus.blaybusbe.domain.task.enums.TaskStatus.IN_PROGRESS);
        }

        return TimerResponse.builder()
                .taskId(task.getId())
                .timerStatus(task.getTimerStatus())
                .timerStartedAt(task.getTimerStartedAt())
                .build();
    }

    /**
     * 타이머 종료 (세션 시간 누적)
     */
    public TimerStopResponse stopTimer(Long menteeId, Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new CustomException(ErrorCode.TASK_NOT_FOUND));

        if (!task.getMentee().getId().equals(menteeId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        if (task.getTimerStatus() == TimerStatus.STOPPED) {
            throw new CustomException(ErrorCode.TIMER_NOT_RUNNING);
        }

        // 세션 시간 계산 (초 단위)
        LocalDateTime startedAt = task.getTimerStartedAt();
        LocalDateTime endedAt = LocalDateTime.now();
        long sessionSeconds = Duration.between(startedAt, endedAt).getSeconds();

        // 누적 시간 갱신
        task.setActualStudyTime(task.getActualStudyTime() + sessionSeconds);
        task.setTimerStatus(TimerStatus.STOPPED);
        task.setTimerStartedAt(null);

        // DailyPlan의 totalStudyTime도 갱신
        DailyPlan dailyPlan = task.getDailyPlan();
        dailyPlan.setTotalStudyTime(dailyPlan.getTotalStudyTime() + sessionSeconds);

        // task_logs에 세션 기록 저장
        TaskLog taskLog = TaskLog.builder()
                .task(task)
                .startAt(startedAt)
                .endAt(endedAt)
                .duration(sessionSeconds)
                .build();
        taskLogRepository.save(taskLog);

        return TimerStopResponse.builder()
                .taskId(task.getId())
                .sessionSeconds(sessionSeconds)
                .accumulatedSeconds(task.getActualStudyTime())
                .build();
    }

    /**
     * 과제별 타이머 로그 목록 조회
     * MENTEE: 본인 과제 로그만 조회 가능
     * MENTOR: 담당 멘티의 과제 로그만 조회 가능
     */
    @Transactional(readOnly = true)
    public List<TaskLogResponse> getTaskLogs(Long userId, Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new CustomException(ErrorCode.TASK_NOT_FOUND));

        validateTaskViewPermission(task, userId);

        return taskLogRepository.findByTaskId(taskId).stream()
                .map(TaskLogResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getTaskListForMentor(Long mentorId, Long menteeId, LocalDate date) {
        // 멘토-멘티 매핑 검증
        if (!menteeInfoRepository.existsByMentorIdAndMenteeId(mentorId, menteeId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        return taskRepository.findByMenteeIdAndTaskDate(menteeId, date).stream()
                .map(response -> TaskResponse.from(response))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getTaskListForMentee(Long menteeId, LocalDate date) {

        return taskRepository.findByMenteeIdAndTaskDate(menteeId, date).stream()
                .map(response -> TaskResponse.from(response))
                .toList();
    }


    // === 헬퍼 메서드 ===

    /**
     * 해당 날짜의 DailyPlan이 없으면 자동 생성
     */
    private DailyPlan findOrCreateDailyPlan(User mentee, LocalDate date) {
        return dailyPlanRepository.findByMenteeIdAndPlanDate(mentee.getId(), date)
                .orElseGet(() -> {
                    DailyPlan newPlan = DailyPlan.builder()
                            .planDate(date)
                            .mentee(mentee)
                            .build();
                    return dailyPlanRepository.save(newPlan);
                });
    }

    /**
     * 과제 조회 권한 검증
     * - 멘티 본인 또는 담당 멘토만 조회 가능
     */
    private void validateTaskViewPermission(Task task, Long userId) {
        Long menteeId = task.getMentee().getId();
        boolean isOwner = menteeId.equals(userId);
        boolean isAssignedMentor = menteeInfoRepository.existsByMentorIdAndMenteeId(userId, menteeId);

        if (!isOwner && !isAssignedMentor) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }
    }

    /**
     * 과제 수정/삭제 권한 검증
     * - 필수 과제(멘토 출제): 멘토만 가능
     * - 자율 과제(멘티 할 일): 멘티 본인만 가능
     */
    private void validateTaskModifyPermission(Task task, Long userId, Role role) {
        if (task.getIsMandatory()) {
            // 필수 과제 → 멘토만 수정/삭제 가능
            if (role != Role.MENTOR) {
                throw new CustomException(ErrorCode.TASK_NOT_MODIFIABLE);
            }
            if (!menteeInfoRepository.existsByMentorIdAndMenteeId(userId, task.getMentee().getId())) {
                throw new CustomException(ErrorCode.TASK_NOT_MODIFIABLE);
            }
        } else {
            // 멘티 할 일 → 멘티 본인만 수정/삭제 가능
            if (!task.getMentee().getId().equals(userId)) {
                throw new CustomException(ErrorCode.TASK_NOT_MODIFIABLE);
            }
        }
    }
}
