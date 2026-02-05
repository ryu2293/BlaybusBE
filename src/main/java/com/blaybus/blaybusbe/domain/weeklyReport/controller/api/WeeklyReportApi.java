package com.blaybus.blaybusbe.domain.weeklyReport.controller.api;

import com.blaybus.blaybusbe.domain.weeklyReport.dto.request.RequestWeeklyReportDto;
import com.blaybus.blaybusbe.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "주간 리포트 API", description = "멘토의 주간 리포트 작성 및 조회 API")
public interface WeeklyReportApi {

    @Operation(summary = "주간 리포트 작성", description = "멘토가 멘티의 주간 학습 리포트를 작성합니다." +
            "멘티 id, 년도, 월, 몇 주차, 주차 시작일, 주차 종료일, 총평, 잘한 점, 보완 점")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "리포트 작성 성공"),
            @ApiResponse(responseCode = "403", description = "작성 권한 없음"),
            @ApiResponse(responseCode = "404", description = "멘티 정보를 찾을 수 없음")
    })
    ResponseEntity<Long> createWeeklyReport(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails user,

            @RequestBody @Valid RequestWeeklyReportDto request
    );

    @Operation(summary = "주간 리포트 수정", description = "멘토가 멘티의 주간 학습 리포트를 작성합니다." +
            "멘티 id, 년도, 월, 몇 주차, 주차 시작일, 주차 종료일, 총평, 잘한 점, 보완 점")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리포트 수정 성공"),
            @ApiResponse(responseCode = "403", description = "작성 권한 없음"),
            @ApiResponse(responseCode = "404", description = "멘티 정보를 찾을 수 없음")
    })
    ResponseEntity<Void> updateWeeklyReport(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails user,

            @Parameter(description = "주간 리포트 id", required = true , example = "1")
            @PathVariable Long reportId,

            @RequestBody RequestWeeklyReportDto request
    );

    @Operation(summary = "주간 리포트 삭제", description = "멘토가 작성한 주간 리포트를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "리포트 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "작성 권한 없음"),
            @ApiResponse(responseCode = "404", description = "멘티 정보를 찾을 수 없음")
    })
    ResponseEntity<Void> deleteWeeklyReport(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails user,

            @Parameter(description = "주간 리포트 id", required = true , example = "1")
            @PathVariable Long reportId
    );
}
