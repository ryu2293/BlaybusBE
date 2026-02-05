package com.blaybus.blaybusbe.domain.plan.controller.api;

import com.blaybus.blaybusbe.domain.plan.dto.request.CreatePlanRequest;
import com.blaybus.blaybusbe.domain.plan.dto.request.PlanFeedbackRequest;
import com.blaybus.blaybusbe.domain.plan.dto.request.UpdatePlanRequest;
import com.blaybus.blaybusbe.domain.plan.dto.response.CalendarDayResponse;
import com.blaybus.blaybusbe.domain.plan.dto.response.PlanFeedbackResponse;
import com.blaybus.blaybusbe.domain.plan.dto.response.PlanResponse;
import com.blaybus.blaybusbe.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "플래너 API", description = "일일 플래너 CRUD 및 멘토 피드백 API")
public interface PlanApi {

    @Operation(summary = "일일 플래너 생성", description = "멘티가 일일 플래너를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "플래너 생성 성공",
                    content = @Content(schema = @Schema(implementation = PlanResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content),
            @ApiResponse(responseCode = "409", description = "해당 날짜에 이미 플래너 존재", content = @Content)
    })
    ResponseEntity<PlanResponse> createPlan(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody CreatePlanRequest request
    );

    @Operation(summary = "날짜별 플래너 조회", description = "날짜별 플래너를 조회합니다. menteeId가 있으면 해당 멘티의 플래너를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "플래너 조회 성공",
                    content = @Content(schema = @Schema(implementation = PlanResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content),
            @ApiResponse(responseCode = "404", description = "플래너 없음", content = @Content)
    })
    ResponseEntity<PlanResponse> getPlan(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam int day,
            @RequestParam(required = false) Long menteeId
    );

    @Operation(summary = "월간 캘린더 조회", description = "월간 캘린더 데이터를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "캘린더 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content)
    })
    ResponseEntity<Page<CalendarDayResponse>> getCalendar(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(required = false) Long menteeId,
            @RequestParam int year,
            @RequestParam int month,
            @Parameter(hidden = true) Pageable pageable
    );

    @Operation(summary = "주간 캘린더 조회", description = "특정 날짜가 포함된 주(월~일)의 플래너와 할 일 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주간 캘린더 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content)
    })
    ResponseEntity<Page<PlanResponse>> getWeeklyCalendar(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(required = false) Long menteeId,
            @Parameter(description = "조회 기준 날짜 (yyyy-MM-dd)") @RequestParam String date,
            @Parameter(hidden = true) Pageable pageable
    );

    @Operation(summary = "플래너 수정", description = "플래너 메모를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "플래너 수정 성공",
                    content = @Content(schema = @Schema(implementation = PlanResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "플래너 없음", content = @Content)
    })
    ResponseEntity<PlanResponse> updatePlan(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long planId,
            @RequestBody UpdatePlanRequest request
    );

    @Operation(summary = "플래너 삭제", description = "플래너를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "플래너 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "플래너 없음", content = @Content)
    })
    ResponseEntity<Void> deletePlan(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long planId
    );

    @Operation(summary = "플래너 피드백 작성", description = "멘토가 플래너에 피드백을 작성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "피드백 작성 성공",
                    content = @Content(schema = @Schema(implementation = PlanFeedbackResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content),
            @ApiResponse(responseCode = "404", description = "플래너 없음", content = @Content),
            @ApiResponse(responseCode = "409", description = "이미 피드백 존재", content = @Content)
    })
    ResponseEntity<PlanFeedbackResponse> createFeedback(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long planId,
            @RequestBody PlanFeedbackRequest request
    );

    @Operation(summary = "플래너 피드백 조회", description = "플래너 피드백을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "피드백 조회 성공",
                    content = @Content(schema = @Schema(implementation = PlanFeedbackResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content),
            @ApiResponse(responseCode = "404", description = "플래너 또는 피드백 없음", content = @Content)
    })
    ResponseEntity<PlanFeedbackResponse> getFeedback(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long planId
    );

    @Operation(summary = "플래너 피드백 수정", description = "멘토가 플래너 피드백을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "피드백 수정 성공",
                    content = @Content(schema = @Schema(implementation = PlanFeedbackResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content),
            @ApiResponse(responseCode = "404", description = "플래너 또는 피드백 없음", content = @Content)
    })
    ResponseEntity<PlanFeedbackResponse> updateFeedback(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long planId,
            @RequestBody PlanFeedbackRequest request
    );

    @Operation(summary = "플래너 피드백 삭제", description = "멘토가 플래너 피드백을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "피드백 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content),
            @ApiResponse(responseCode = "404", description = "플래너 또는 피드백 없음", content = @Content)
    })
    ResponseEntity<Void> deleteFeedback(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long planId
    );
}
