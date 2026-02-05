package com.blaybus.blaybusbe.domain.weeklyReport.controller;

import com.blaybus.blaybusbe.domain.weeklyReport.controller.api.WeeklyReportApi;
import com.blaybus.blaybusbe.domain.weeklyReport.dto.request.RequestWeeklyReportDto;
import com.blaybus.blaybusbe.domain.weeklyReport.dto.response.ResponseWeeklyReportDto;
import com.blaybus.blaybusbe.domain.weeklyReport.service.WeeklyReportService;
import com.blaybus.blaybusbe.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class WeeklyReportController implements WeeklyReportApi {

    private final WeeklyReportService weeklyReportService;

    /**
     * 멘토가 주차 보고서(총평, 잘한점, 보완점)를 작성합니다
     *
     * @param user 토큰 추출
     * @param request 요청 값
     */
    @Override
    @PostMapping("/mentor/weekly-report")
    public ResponseEntity<Long> createWeeklyReport(
            @AuthenticationPrincipal CustomUserDetails user,

            @RequestBody @Valid
            RequestWeeklyReportDto request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(weeklyReportService.createWeeklyReport(user.getId(), request));
    }

    /**
     * 멘토가 주차 보고서(총평, 잘한점, 보완점)를 수정합니다
     *
     * @param user 토큰 추출
     * @param reportId 주간 보고서 id
     * @param request 요청 값
     */
    @Override
    @PatchMapping("/mentor/weekly-report/{reportId}")
    public ResponseEntity<Void> updateWeeklyReport(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long reportId,
            @RequestBody RequestWeeklyReportDto request
    ) {
        weeklyReportService.updateWeeklyReport(user.getId(), reportId, request);
        return ResponseEntity.ok().build();
    }

    /**
     * 멘토가 주차 보고서(총평, 잘한점, 보완점)를 삭제합니다
     *
     * @param user 토큰 추출
     * @param reportId 주간 보고서 id
     */
    @Override
    @DeleteMapping("/mentor/weekly-report/{reportId}")
    public ResponseEntity<Void> deleteWeeklyReport(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long reportId
    ) {
        weeklyReportService.deleteWeeklyReport(user.getId(), reportId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 주간 보고서 상세 조회
     *
     * @param user 토큰 추출
     * @param reportId 조회할 주간 보고서
     */
    @Override
    @GetMapping("/weekly-reports/{reportId}")
    public ResponseEntity<ResponseWeeklyReportDto> getWeeklyReport(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long reportId
    ) {
        return ResponseEntity.ok(weeklyReportService.getWeeklyReport(user.getId(), reportId));
    }

    /**
     * 연도/월별 주간보고서 목록 조회
     *
     * @param user 토큰 추출
     * @param menteeId 멘티 id (멘토가 조회할 때만 입력)
     * @param year 연도
     * @param month 월별
     * @param pageable 페이지네이션
     */
    @Override
    @GetMapping("/weekly-reports")
    public ResponseEntity<Page<ResponseWeeklyReportDto>> getWeeklyReportPage(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(required = false) Long menteeId,
            @RequestParam Integer year,
            @RequestParam Integer month,
            Pageable pageable
    ) {
        return ResponseEntity.ok(weeklyReportService.getWeeklyReportPage(user.getId(), menteeId, year, month, pageable));
    }
}
