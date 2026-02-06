package com.blaybus.blaybusbe.domain.zoomfeedback.controller.api;

import com.blaybus.blaybusbe.domain.zoomfeedback.dto.request.CreateZoomFeedbackRequest;
import com.blaybus.blaybusbe.domain.zoomfeedback.dto.response.ZoomFeedbackResponse;
import com.blaybus.blaybusbe.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "줌 피드백 API", description = "줌으로 상담 후 피드백을 작성합니다.")
public interface ZoomFeedbackApi {

    @Operation(summary = "줌 피드백 작성", description = "줌 상담 후 여러 피드백을 작성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "줌 피드백 등록 성공",
                    content = @Content(schema = @Schema(implementation = Long.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content),
            @ApiResponse(responseCode = "403", description = "접근 권한이 없는 사용자", content = @Content),
    })
    ResponseEntity<Long> createZoomFeedback(
            @Parameter(hidden = true) CustomUserDetails user,
            @Parameter(description = "멘티 id", required = true , example = "1")
            @PathVariable Long menteeId,
            CreateZoomFeedbackRequest request
    );

    @Operation(summary = "줌 피드백 상세 조회", description = "작성된 줌 피드백의 상세 내용을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "줌 조회 성공",
                    content = @Content(schema = @Schema(implementation = ZoomFeedbackResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content),
            @ApiResponse(responseCode = "403", description = "접근 권한이 없는 사용자", content = @Content),
    })
    ResponseEntity<ZoomFeedbackResponse> getZoomFeedback(
            @Parameter(hidden = true) CustomUserDetails user,
            @Parameter(description = "피드백 id", required = true , example = "1")
            @PathVariable Long feedbackId
    );
}
