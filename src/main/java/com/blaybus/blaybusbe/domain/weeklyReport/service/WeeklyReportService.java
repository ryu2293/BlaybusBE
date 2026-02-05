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

}
