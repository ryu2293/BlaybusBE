package com.blaybus.blaybusbe.domain.plan.service;

import com.blaybus.blaybusbe.domain.mentoring.repository.MenteeInfoRepository;
import com.blaybus.blaybusbe.domain.plan.dto.request.CreatePlanRequest;
import com.blaybus.blaybusbe.domain.plan.dto.request.PlanFeedbackRequest;
import com.blaybus.blaybusbe.domain.plan.dto.request.UpdatePlanRequest;
import com.blaybus.blaybusbe.domain.plan.dto.response.*;
import com.blaybus.blaybusbe.domain.plan.entity.DailyPlan;
import com.blaybus.blaybusbe.domain.plan.repository.DailyPlanRepository;
import com.blaybus.blaybusbe.domain.task.entity.Task;
import com.blaybus.blaybusbe.domain.task.enums.Subject;
import java.util.Collections;
import java.time.YearMonth;
import java.time.LocalDate;
import com.blaybus.blaybusbe.domain.task.enums.TaskStatus;
import com.blaybus.blaybusbe.domain.task.repository.TaskRepository;
import com.blaybus.blaybusbe.domain.notification.event.NotificationEvent;
import com.blaybus.blaybusbe.domain.notification.enums.NotificationType;
import com.blaybus.blaybusbe.domain.user.entity.User;
import com.blaybus.blaybusbe.domain.user.enums.Role;
import com.blaybus.blaybusbe.domain.user.repository.UserRepository;
import com.blaybus.blaybusbe.global.exception.CustomException;
import com.blaybus.blaybusbe.global.exception.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blaybus.blaybusbe.domain.plan.dto.response.TaskSummaryResponse;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PlanService {

    private final DailyPlanRepository dailyPlanRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final MenteeInfoRepository menteeInfoRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 일일 플래너 생성 (멘티만 가능)
     */
    public PlanResponse createPlan(Long menteeId, Role role, CreatePlanRequest request) {
        if (role != Role.MENTEE) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        User mentee = userRepository.findById(menteeId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (dailyPlanRepository.existsByMenteeIdAndPlanDate(menteeId, request.planDate())) {
            throw new CustomException(ErrorCode.PLAN_DUPLICATE_DATE);
        }

        DailyPlan plan = DailyPlan.builder()
                .planDate(request.planDate())
                .dailyMemo(request.dailyMemo())
                .mentee(mentee)
                .build();

        dailyPlanRepository.save(plan);
        return PlanResponse.from(plan, List.of());
    }

    /**
     * 날짜별 플래너 조회 (본인)
     */
    @Transactional(readOnly = true)
    public PlanResponse getPlanByDate(Long menteeId, LocalDate date) {
        DailyPlan plan = dailyPlanRepository.findByMenteeIdAndPlanDate(menteeId, date)
                .orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));

        List<Task> tasks = taskRepository.findByDailyPlanId(plan.getId());
        List<DailyPlanTaskResponse> dailyPlanTasks = tasks.stream().map(DailyPlanTaskResponse::from).toList();
        return PlanResponse.from(plan, dailyPlanTasks);
    }

    /**
     * 멘티 플래너 조회 (멘토용)
     */
    @Transactional(readOnly = true)
    public PlanResponse getMenteePlanByDate(Long menteeId, LocalDate date) {
        userRepository.findById(menteeId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        DailyPlan plan = dailyPlanRepository.findByMenteeIdAndPlanDate(menteeId, date)
                .orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));

        List<Task> tasks = taskRepository.findByDailyPlanId(plan.getId());
        List<DailyPlanTaskResponse> dailyPlanTasks = tasks.stream().map(DailyPlanTaskResponse::from).toList();
        return PlanResponse.from(plan, dailyPlanTasks);
    }

    /**
     * 월간 캘린더 조회 (과목 필터, 미완료만 필터)
     */
    @Transactional(readOnly = true)
    public Page<CalendarDayResponse> getCalendar(Long menteeId, int year, int month,
                                                  Subject subject, Boolean incompleteOnly,
                                                  Pageable pageable) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        // 멘티의 모든 DailyPlan (필터 적용 전)을 먼저 가져옵니다.
        // 이렇게 하면 각 DailyPlan에 연결된 Task를 효율적으로 조회할 수 있습니다.
        List<DailyPlan> allPlansInMonth = dailyPlanRepository.findByMenteeIdAndPlanDateBetween(menteeId, startDate, endDate);
        List<Long> dailyPlanIds = allPlansInMonth.stream().map(DailyPlan::getId).toList();

        // 필터 조건에 맞는 Task를 모두 조회합니다.
        List<Task> allTasksInMonth;
        if (subject != null && Boolean.TRUE.equals(incompleteOnly)) {
            allTasksInMonth = taskRepository.findByDailyPlanIdInAndSubjectAndStatusNot(
                    dailyPlanIds, subject, TaskStatus.DONE
            );
        } else if (subject != null) {
            allTasksInMonth = taskRepository.findByDailyPlanIdInAndSubject(
                    dailyPlanIds, subject
            );
        } else if (Boolean.TRUE.equals(incompleteOnly)) {
            allTasksInMonth = taskRepository.findByDailyPlanIdInAndStatusNot(
                    dailyPlanIds, TaskStatus.DONE
            );
        } else {
            allTasksInMonth = taskRepository.findByDailyPlanIdIn(dailyPlanIds);
        }

        // DailyPlan ID별로 Task를 그룹화합니다.
        java.util.Map<Long, List<TaskSummaryResponse>> tasksByDailyPlanId = allTasksInMonth.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        task -> task.getDailyPlan().getId(),
                        java.util.stream.Collectors.mapping(TaskSummaryResponse::from, java.util.stream.Collectors.toList())
                ));

        // 필터링된 DailyPlan에 Task 리스트를 포함하여 CalendarDayResponse로 변환합니다.
        return dailyPlanRepository.findByMenteeIdAndPlanDateBetween(menteeId, startDate, endDate, pageable)
                .map(plan -> {
                    List<TaskSummaryResponse> tasks = tasksByDailyPlanId.getOrDefault(plan.getId(), Collections.emptyList());
                    return CalendarDayResponse.from(plan, tasks);
                });
    }

    /**
     * 플래너 수정 (메모)
     */
    public PlanResponse updatePlan(Long menteeId, Long planId, UpdatePlanRequest request) {
        DailyPlan plan = dailyPlanRepository.findById(planId)
                .orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));

        if (!plan.getMentee().getId().equals(menteeId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        plan.setDailyMemo(request.dailyMemo());
        List<Task> tasks = taskRepository.findByDailyPlanId(plan.getId());
        List<DailyPlanTaskResponse> dailyPlanTasks = tasks.stream().map(DailyPlanTaskResponse::from).toList();
        return PlanResponse.from(plan, dailyPlanTasks);
    }

    /**
     * 플래너 삭제
     */
    public void deletePlan(Long menteeId, Long planId) {
        DailyPlan plan = dailyPlanRepository.findById(planId)
                .orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));

        if (!plan.getMentee().getId().equals(menteeId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        dailyPlanRepository.delete(plan);
    }

    /**
     * 플래너 피드백 작성 (멘토만 가능)
     */
    public PlanFeedbackResponse createFeedback(Long mentorId, Role role, Long planId, PlanFeedbackRequest request) {
        if (role != Role.MENTOR) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        DailyPlan plan = dailyPlanRepository.findById(planId)
                .orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));

        // 멘토-멘티 매핑 확인
        if (!menteeInfoRepository.existsByMentorIdAndMenteeId(mentorId, plan.getMentee().getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        if (plan.getMentorFeedback() != null) {
            throw new CustomException(ErrorCode.PLAN_FEEDBACK_ALREADY_EXISTS);
        }

        User mentor = userRepository.findById(mentorId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        plan.setMentorFeedback(request.content());

        // 멘티에게 플래너 피드백 알림
        eventPublisher.publishEvent(new NotificationEvent(
                NotificationType.PLAN_FEEDBACK,
                plan.getMentee().getId(),
                String.format("%s 멘토님이 플래너 피드백을 작성했습니다.", mentor.getName())
        ));

        return PlanFeedbackResponse.from(plan, mentor.getName());
    }

    /**
     * 플래너 피드백 조회 (멘티 본인 또는 담당 멘토만 가능)
     */
    @Transactional(readOnly = true)
    public PlanFeedbackResponse getFeedback(Long userId, Long planId, String mentorName) {
        DailyPlan plan = dailyPlanRepository.findById(planId)
                .orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));

        Long menteeId = plan.getMentee().getId();
        boolean isOwner = menteeId.equals(userId);
        boolean isAssignedMentor = menteeInfoRepository.existsByMentorIdAndMenteeId(userId, menteeId);
        if (!isOwner && !isAssignedMentor) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        if (plan.getMentorFeedback() == null) {
            throw new CustomException(ErrorCode.PLAN_FEEDBACK_NOT_FOUND);
        }

        return PlanFeedbackResponse.from(plan, mentorName);
    }

    /**
     * 플래너 피드백 수정 (멘토만 가능)
     */
    public PlanFeedbackResponse updateFeedback(Long mentorId, Role role, Long planId, PlanFeedbackRequest request) {
        if (role != Role.MENTOR) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        DailyPlan plan = dailyPlanRepository.findById(planId)
                .orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));

        // 멘토-멘티 매핑 확인
        if (!menteeInfoRepository.existsByMentorIdAndMenteeId(mentorId, plan.getMentee().getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        if (plan.getMentorFeedback() == null) {
            throw new CustomException(ErrorCode.PLAN_FEEDBACK_NOT_FOUND);
        }

        User mentor = userRepository.findById(mentorId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        plan.setMentorFeedback(request.content());
        return PlanFeedbackResponse.from(plan, mentor.getName());
    }

    /**
     * 플래너 피드백 삭제 (멘토만 가능)
     */
    public void deleteFeedback(Long mentorId, Role role, Long planId) {
        if (role != Role.MENTOR) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        DailyPlan plan = dailyPlanRepository.findById(planId)
                .orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));

        // 멘토-멘티 매핑 확인
        if (!menteeInfoRepository.existsByMentorIdAndMenteeId(mentorId, plan.getMentee().getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        if (plan.getMentorFeedback() == null) {
            throw new CustomException(ErrorCode.PLAN_FEEDBACK_NOT_FOUND);
        }

        plan.setMentorFeedback(null);
    }
}
