package com.blaybus.blaybusbe.domain.weeklyReport.service;

import com.blaybus.blaybusbe.domain.mentoring.entity.MenteeInfo;
import com.blaybus.blaybusbe.domain.mentoring.repository.MenteeInfoRepository;
import com.blaybus.blaybusbe.domain.notification.event.NotificationEvent;
import com.blaybus.blaybusbe.domain.notification.enums.NotificationType;
import com.blaybus.blaybusbe.domain.weeklyReport.dto.request.RequestWeeklyReportDto;
import com.blaybus.blaybusbe.domain.weeklyReport.dto.response.ResponseWeeklyReportDto;
import com.blaybus.blaybusbe.domain.weeklyReport.entity.WeeklyReport;
import com.blaybus.blaybusbe.domain.weeklyReport.repository.WeeklyReportRepository;
import com.blaybus.blaybusbe.global.exception.CustomException;
import com.blaybus.blaybusbe.global.exception.error.ErrorCode;
import com.blaybus.blaybusbe.domain.user.entity.User;
import com.blaybus.blaybusbe.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class WeeklyReportService {

    private final WeeklyReportRepository weeklyReportRepository;
    private final MenteeInfoRepository menteeInfoRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

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

        Long reportId = weeklyReportRepository.save(weeklyReport).getId();

        // 멘티에게 주간 리포트 알림
        User mentor = userRepository.findById(mentorId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        eventPublisher.publishEvent(new NotificationEvent(
                NotificationType.WEEKLY_REPORT,
                request.menteeId(),
                String.format("%s 멘토님이 주간 리포트를 작성했습니다.", mentor.getName())
        ));

        return reportId;
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

    /**
     * 주간 보고서 상세 조회
     *
     * @param userId 조회 요청한 유저 id
     * @param reportId 조회할 주간 보고서
     */
    @Transactional(readOnly = true)
    public ResponseWeeklyReportDto getWeeklyReport(Long userId, Long reportId) {
        WeeklyReport report = weeklyReportRepository.findById(reportId)
                .orElseThrow(() -> new CustomException(ErrorCode.REPORT_NOT_FOUND));

        // 리포트의 멘티 혹은 멘토인지 확인
        validateAccessAuthority(userId, report.getMenteeInfo());

        return ResponseWeeklyReportDto.from(report);
    }

    /**
     * 연도/월별 주간보고서 목록 조회
     *
     * @param userId 조회 요청한 유저 id
     * @param menteeId 멘티 id (멘토가 조회할 때만 입력)
     * @param year 연도
     * @param month 월별
     * @param pageable 페이지네이션
     */
    @Transactional(readOnly = true)
    public Page<ResponseWeeklyReportDto> getWeeklyReportPage(Long userId, Long menteeId, Integer year, Integer month, Pageable pageable) {
        MenteeInfo menteeInfo;

        if (menteeId != null) {
            // 멘토가 특정 멘티의 리포트를 조회하는 경우
            menteeInfo = menteeInfoRepository.findByMentorIdAndMenteeId(userId, menteeId)
                    .orElseThrow(() -> new CustomException(ErrorCode.MENTEE_INFO_NOT_FOUND));
        } else {
            // 멘티 본인이 자신의 리포트를 조회하는 경우
            menteeInfo = menteeInfoRepository.findByMenteeId(userId)
                    .orElseThrow(() -> new CustomException(ErrorCode.MENTEE_INFO_NOT_FOUND));
        }

        return weeklyReportRepository.findAllByMenteeInfoAndReportYearAndReportMonth(menteeInfo, year, month, pageable)
                .map(response -> ResponseWeeklyReportDto.from(response));
    }

    /**
     * 조회한 사람이 멘토-멘티 관계가 아니라면 예외 처리
     *
     * @param userId
     * @param menteeInfo
     */
    private void validateAccessAuthority(Long userId, MenteeInfo menteeInfo) {
        boolean isMentor = menteeInfo.getMentor().getId().equals(userId);
        boolean isMentee = menteeInfo.getMentee().getId().equals(userId);

        if (!isMentor && !isMentee) {
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
        }
    }

    /**
     * 맨토가 멘토-멘티 관계가 아니라면 예외 처리
     *
     * @param mentorId
     * @param report
     */
    private void validateMentorAuthority(Long mentorId, WeeklyReport report) {
        if (!report.getMenteeInfo().getMentor().getId().equals(mentorId)) {
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
        }
    }
}
