package com.blaybus.blaybusbe.domain.dashboard.controller.api;

import com.blaybus.blaybusbe.domain.dashboard.dto.response.MenteeDashboardResponse;
import com.blaybus.blaybusbe.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "대시보드 API", description = "멘토 및 멘티용 학습 현황 대시보드 API")
public interface DashboardApi {

    @Operation(summary = "멘토용: 멘티 상세 대시보드 조회", description = "### 특정 멘티의 주간/월간 학습 지표를 조회합니다. <br><br>" +
            "**[지표 상세 정의]** <br>" +
            "- **submittedTasksCount**: 조회 기간 내 제출되었으나 멘토 확인(피드백)이 없는 필수 과제 수 <br>" +
            "- **remainingTasksCount**: 기간 상관없이 멘티가 아직 제출하지 않은 필수 과제 수 <br>" +
            "- **feedbackQuestionsCount**: 멘토가 확인하지 않은 멘티의 피드백 답글(멘티가 피드백에 대한 질문) 수 <br>" +
            "- **todayPlannerTodoCount**: 오늘 제출된 플래너 중 멘토 피드백이 없는 상태 (0 또는 1)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "멘티 정보를 찾을 수 없음")
    })
    ResponseEntity<MenteeDashboardResponse> getMenteeDashboard(
            @Parameter(hidden = true) CustomUserDetails user,
            @Parameter(description = "조회할 멘티 ID", example = "1") Long menteeId,
            @Parameter(description = "조회 기간 (WEEK, MONTH)", example = "WEEK") String type
    );

    @Operation(summary = "멘티용: 마이페이지 대시보드 조회", description = "### 특정 멘티의 주간/월간 학습 지표를 조회합니다. <br><br>" +
            "**[지표 상세 정의]** <br>" +
            "- **submittedTasksCount**: 조회 기간 내 제출되었으나 멘토 확인(피드백)이 없는 필수 과제 수 <br>" +
            "- **remainingTasksCount**: 기간 상관없이 멘티가 아직 제출하지 않은 필수 과제 수 <br>" +
            "- **feedbackQuestionsCount**: 멘토가 확인하지 않은 멘티의 피드백 답글(멘티가 피드백에 대한 질문) 수 <br>" +
            "- **todayPlannerTodoCount**: 오늘 제출된 플래너 중 멘토 피드백이 없는 상태 (0 또는 1)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    ResponseEntity<MenteeDashboardResponse> getMyDashboard(
            @Parameter(hidden = true) CustomUserDetails user,
            @Parameter(description = "조회 기간 (WEEK, MONTH)", example = "WEEK") String type
    );
}
