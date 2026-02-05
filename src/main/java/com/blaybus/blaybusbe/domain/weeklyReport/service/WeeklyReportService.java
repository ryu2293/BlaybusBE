package com.blaybus.blaybusbe.domain.weeklyReport.service;

import com.blaybus.blaybusbe.domain.mentoring.entity.MenteeInfo;
import com.blaybus.blaybusbe.domain.mentoring.repository.MenteeInfoRepository;
import com.blaybus.blaybusbe.domain.weeklyReport.dto.request.RequestWeeklyReportDto;
import com.blaybus.blaybusbe.domain.weeklyReport.entity.WeeklyReport;
import com.blaybus.blaybusbe.domain.weeklyReport.repository.WeeklyReportRepository;
import com.blaybus.blaybusbe.global.exception.CustomException;
import com.blaybus.blaybusbe.global.exception.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class WeeklyReportService {

    private final WeeklyReportRepository weeklyReportRepository;
    private final MenteeInfoRepository menteeInfoRepository;

    /**
     * 멘토가 주차 보고서(총평, 잘한점, 보완점)를 작성합니다
     *
     * @param mentorId 멘토 id
     * @param request 요청 값
     */
    public Long createWeeklyReport(Long mentorId, RequestWeeklyReportDto request) {
        // 멘토-멘티 관계 및 MenteeInfo 조회
        MenteeInfo menteeInfo = menteeInfoRepository.findByMentorIdAndMenteeId(mentorId, request.menteeId())
                .orElseThrow(() -> new CustomException(ErrorCode.MENTEE_INFO_NOT_FOUND));

        // 주간 레포트 생성
        WeeklyReport weeklyReport = request.dtoToEntity(menteeInfo);

        return weeklyReportRepository.save(weeklyReport).getId();
    }

    /**
     * 멘토가 주차 보고서(총평, 잘한점, 보완점)를 수정합니다
     *
     * @param mentorId 멘토 id
     * @param reportId 주간 보고서 id
     * @param request 요청 값
     */
    public void updateWeeklyReport(Long mentorId, Long reportId, RequestWeeklyReportDto request) {
        WeeklyReport report = weeklyReportRepository.findById(reportId)
                .orElseThrow(() -> new CustomException(ErrorCode.REPORT_NOT_FOUND));

        // 레포트와 연결된 멘티정보의 담당 멘토가 요청자와 일치하는지 확인
        validateMentorAuthority(mentorId, report);

        report.updateReport(
                request.reportYear(),
                request.reportMonth(),
                request.weekNumber(),
                request.startDate(),
                request.endDate(),
                request.overallFeedback(),
                request.strengths(),
                request.weaknesses()
        );
    }

    /**
     * 멘토가 주차 보고서(총평, 잘한점, 보완점)를 삭제합니다
     *
     * @param mentorId 멘토 id
     * @param reportId 주간 보고서 id
     */
    public void deleteWeeklyReport(Long mentorId, Long reportId) {
        WeeklyReport report = weeklyReportRepository.findById(reportId)
                .orElseThrow(() -> new CustomException(ErrorCode.REPORT_NOT_FOUND));

        // 레포트와 연결된 멘티정보의 담당 멘토가 요청자와 일치하는지 확인
        validateMentorAuthority(mentorId, report);

        weeklyReportRepository.delete(report);
    }

    private void validateMentorAuthority(Long mentorId, WeeklyReport report) {
        if (!report.getMenteeInfo().getMentor().getId().equals(mentorId)) {
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
        }
    }
}
