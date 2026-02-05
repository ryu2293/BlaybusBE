package com.blaybus.blaybusbe.domain.task.service;

import com.blaybus.blaybusbe.domain.mentoring.repository.MenteeInfoRepository;
import com.blaybus.blaybusbe.domain.plan.entity.DailyPlan;
import com.blaybus.blaybusbe.domain.plan.repository.DailyPlanRepository;
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
import com.blaybus.blaybusbe.global.exception.CustomException;
import com.blaybus.blaybusbe.global.exception.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskLogRepository taskLogRepository;
    private final DailyPlanRepository dailyPlanRepository;
    private final UserRepository userRepository;
    private final MenteeInfoRepository menteeInfoRepository;

    /**
     * 멘토 과제 출제 (is_mandatory=true)
     * - date만 전달: 단일 과제 생성
     * - startDate + endDate + daysOfWeek 전달: 반복 과제 일괄 생성 (recurringGroupId 부여)
     */
    public RecurringTaskResponse createMentorTask(Long mentorId, Long menteeId, CreateMentorTaskRequest request) {
        // 멘토-멘티 매핑 검증
        if (!menteeInfoRepository.existsByMentorIdAndMenteeId(mentorId, menteeId)) {
            throw new CustomException(ErrorCode.MENTEE_INFO_NOT_FOUND);
        }

        User mentee = userRepository.findById(menteeId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (request.isRecurring()) {
            return createRecurringTasks(mentee, request);
        }
        return createSingleTask(mentee, request);
    }

    private RecurringTaskResponse createSingleTask(User mentee, CreateMentorTaskRequest request) {
        DailyPlan dailyPlan = findOrCreateDailyPlan(mentee, request.date());

        Task task = Task.builder()
                .subject(request.subject())
                .title(request.title())
                .taskDate(request.date())
                .isMandatory(true)
                .weaknessId(request.weaknessId())
                .dailyPlan(dailyPlan)
                .mentee(mentee)
                .build();

        taskRepository.save(task);

        return RecurringTaskResponse.builder()
                .taskCount(1)
                .tasks(List.of(TaskResponse.from(task)))
                .build();
    }

    private RecurringTaskResponse createRecurringTasks(User mentee, CreateMentorTaskRequest request) {
        String groupId = UUID.randomUUID().toString();
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
                            .weaknessId(request.weaknessId())
                            .recurringGroupId(groupId)
                            .dailyPlan(dailyPlan)
                            .mentee(mentee)
                            .build();

                    taskRepository.save(task);
                    createdTasks.add(task);
                }
            }
            current = current.plusDays(1);
        }

        return RecurringTaskResponse.builder()
                .recurringGroupId(groupId)
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
    public TaskResponse getTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new CustomException(ErrorCode.TASK_NOT_FOUND));
        return TaskResponse.from(task);
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

        // 세션 시간 계산 (분 단위)
        LocalDateTime startedAt = task.getTimerStartedAt();
        LocalDateTime endedAt = LocalDateTime.now();
        long sessionMinutes = Duration.between(startedAt, endedAt).toMinutes();
        int sessionMin = (int) sessionMinutes;

        // 누적 시간 갱신
        task.setActualStudyTime(task.getActualStudyTime() + sessionMin);
        task.setTimerStatus(TimerStatus.STOPPED);
        task.setTimerStartedAt(null);

        // DailyPlan의 totalStudyTime도 갱신
        DailyPlan dailyPlan = task.getDailyPlan();
        dailyPlan.setTotalStudyTime(dailyPlan.getTotalStudyTime() + sessionMin);

        // task_logs에 세션 기록 저장
        TaskLog taskLog = TaskLog.builder()
                .task(task)
                .startAt(startedAt)
                .endAt(endedAt)
                .duration(sessionMin)
                .build();
        taskLogRepository.save(taskLog);

        return TimerStopResponse.builder()
                .taskId(task.getId())
                .sessionMinutes(sessionMin)
                .accumulatedMinutes(task.getActualStudyTime())
                .build();
    }

    /**
     * 반복 과제 그룹 삭제
     */
    public void deleteRecurringTasks(Long mentorId, String recurringGroupId) {
        List<Task> tasks = taskRepository.findByRecurringGroupId(recurringGroupId);

        if (tasks.isEmpty()) {
            throw new CustomException(ErrorCode.RECURRING_GROUP_NOT_FOUND);
        }

        // 멘토-멘티 매핑 검증 (첫 번째 과제의 멘티로 확인)
        Long menteeId = tasks.get(0).getMentee().getId();
        if (!menteeInfoRepository.existsByMentorIdAndMenteeId(mentorId, menteeId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        taskRepository.deleteAll(tasks);
    }

    /**
     * 타이머 기록 조회
     */
    @Transactional(readOnly = true)
    public List<TaskLogResponse> getTaskLogs(Long taskId) {
        taskRepository.findById(taskId)
                .orElseThrow(() -> new CustomException(ErrorCode.TASK_NOT_FOUND));

        return taskLogRepository.findByTaskId(taskId).stream()
                .map(TaskLogResponse::from)
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
