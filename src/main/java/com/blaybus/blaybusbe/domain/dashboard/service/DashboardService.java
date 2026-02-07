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

    public MenteeDashboardResponse getMenteeDashboard(Long mentorId, Long menteeId) {
        // 관계 및 기본 정보 조회
        MenteeInfo info = menteeInfoRepository.findByMentorIdAndMenteeId(mentorId, menteeId)
                .orElseThrow(() -> new CustomException(ErrorCode.MENTEE_INFO_NOT_FOUND));

        // 지표 실시간 카운트 (과제 제출, 남은 과제, 플래너 제출, 피드백 질문)
        long submitted = taskRepository.countByMenteeIdAndStatusAndIsMentorChecked(menteeId, TaskStatus.DONE, false);
        long remaining = taskRepository.countByMenteeIdAndIsMandatoryAndStatusNot(menteeId, true, TaskStatus.DONE);
        long questions = answerRepository.countUncheckedQuestionsByMentee(menteeId);

        // 오늘 피드백 줄 플래너가 있다면 1, 없으면 0
        long plannerTodo = dailyPlanRepository.existsByMenteeIdAndPlanDateAndMentorFeedbackIsNull(menteeId, LocalDate.now()) ? 1 : 0;

        // 과목별 진행률 계산
        double korRate = calculateProgressRate(menteeId, Subject.KOREAN);
        double mathRate = calculateProgressRate(menteeId, Subject.MATH);
        double engRate = calculateProgressRate(menteeId, Subject.ENGLISH);

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

    private double calculateProgressRate(Long menteeId, Subject subject) {
        long total = taskRepository.countByMenteeIdAndSubjectAndIsMandatory(menteeId, subject, true);
        if (total == 0) return 0.0;
        long checked = taskRepository.countByMenteeIdAndSubjectAndIsMandatoryAndIsMentorChecked(menteeId, subject, true, true);
        return (double) checked / total * 100;
    }
}
