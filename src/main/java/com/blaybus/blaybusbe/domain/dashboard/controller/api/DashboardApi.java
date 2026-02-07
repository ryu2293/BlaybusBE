package com.blaybus.blaybusbe.domain.dashboard.controller.api;

import com.blaybus.blaybusbe.domain.dashboard.dto.response.MenteeDashboardResponse;
import com.blaybus.blaybusbe.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "대시보드 API", description = "멘토용 멘티 상세 대시보드 조회 API")
public interface DashboardApi {

    @Operation(summary = "멘티 상세 대시보드 조회", description = "특정 멘티의 학습 현황, 진행률 및 주요 지표를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "멘토 대시보드 조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.")
    })
    ResponseEntity<MenteeDashboardResponse> getMenteeDashboard(
            @Parameter(hidden = true) CustomUserDetails user,
            @Parameter(description = "멘티 id") Long menteeId
    );
}
