package com.blaybus.blaybusbe.domain.weeklyReport.controller;

import com.blaybus.blaybusbe.domain.weeklyReport.controller.api.WeeklyReportApi;
import com.blaybus.blaybusbe.domain.weeklyReport.dto.request.RequestWeeklyReportDto;
import com.blaybus.blaybusbe.domain.weeklyReport.service.WeeklyReportService;
import com.blaybus.blaybusbe.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
}
