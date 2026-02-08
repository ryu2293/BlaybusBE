package com.blaybus.blaybusbe.domain.dashboard.service;

import com.blaybus.blaybusbe.domain.comment.repository.AnswerRepository;
import com.blaybus.blaybusbe.domain.dashboard.dto.response.MenteeDashboardResponse;
import com.blaybus.blaybusbe.domain.mentoring.entity.MenteeInfo;
import com.blaybus.blaybusbe.domain.mentoring.repository.MenteeInfoRepository;
import com.blaybus.blaybusbe.domain.plan.repository.DailyPlanRepository;
import com.blaybus.blaybusbe.domain.task.enums.Subject;
import com.blaybus.blaybusbe.domain.task.enums.TaskStatus;
import com.blaybus.blaybusbe.domain.task.repository.TaskRepository;
import com.blaybus.blaybusbe.global.exception.CustomException;
import com.blaybus.blaybusbe.global.exception.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional
@RequiredArgsConstructor
public class DashboardService {
    private final TaskRepository taskRepository;
    private final MenteeInfoRepository menteeInfoRepository;
    private final AnswerRepository answerRepository;
    private final DailyPlanRepository dailyPlanRepository;

    /**
     * 멘티의 대시보드를 조회
     *
     * @param menteeId 멘티 id
     * @param type 1주 or 1달
     */
    public MenteeDashboardResponse getMenteeDashboard(Long menteeId, String type) {
        // 기간 설정 (주/월)
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = type.equalsIgnoreCase("MONTH")
                ? endDate.minusMonths(1)
                : endDate.minusWeeks(1);

        // 기본 정보 조회
        MenteeInfo info = menteeInfoRepository.findByMenteeId(menteeId)
                .orElseThrow(() -> new CustomException(ErrorCode.MENTEE_INFO_NOT_FOUND));

        // 기간 필터링 지표 계산 (과제 제출, 남은 과제, 피드백 질문)
        long submitted = taskRepository.countByMenteeIdAndStatusAndIsMentorCheckedAndTaskDateBetween(
                menteeId, TaskStatus.DONE, false, startDate, endDate);
        long remaining = taskRepository.countByMenteeIdAndIsMandatoryAndStatusNot(menteeId, true, TaskStatus.DONE);
        long questions = answerRepository.countUncheckedQuestionsByMentee(menteeId);

        // 오늘 피드백 줄 플래너 확인 (0 또는 1)
        long plannerTodo = dailyPlanRepository.existsByMenteeIdAndPlanDateAndMentorFeedbackIsNull(menteeId, endDate) ? 1 : 0;

        // 기간 필터링 진행률 계산
        double korRate = calculateRateWithDate(menteeId, Subject.KOREAN, startDate, endDate);
        double mathRate = calculateRateWithDate(menteeId, Subject.MATH, startDate, endDate);
        double engRate = calculateRateWithDate(menteeId, Subject.ENGLISH, startDate, endDate);

        return new MenteeDashboardResponse(
                info.getMentee().getId(),
                info.getMentee().getName(),
                info.getMentee().getProfileImgUrl(),
                info.getSchoolName(),
                submitted,
                remaining,
                questions,
                plannerTodo,
                korRate,
                mathRate,
                engRate
        );
    }

    private double calculateRateWithDate(Long menteeId, Subject subject, LocalDate start, LocalDate end) {
        long total = taskRepository.countByMenteeIdAndSubjectAndIsMandatoryAndTaskDateBetween(menteeId, subject, true, start, end); //
        if (total == 0) return 0.0;
        long checked = taskRepository.countByMenteeIdAndSubjectAndIsMandatoryAndIsMentorCheckedAndTaskDateBetween(
                menteeId, subject, true, true, start, end); //
        return (double) checked / total * 100;
    }
}
