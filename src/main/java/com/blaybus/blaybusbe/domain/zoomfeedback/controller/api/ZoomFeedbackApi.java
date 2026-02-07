package com.blaybus.blaybusbe.domain.zoomfeedback.controller.api;

import com.blaybus.blaybusbe.domain.zoomfeedback.dto.request.CreateZoomFeedbackRequest;
import com.blaybus.blaybusbe.domain.zoomfeedback.dto.response.ZoomFeedbackListResponse;
import com.blaybus.blaybusbe.domain.zoomfeedback.dto.response.ZoomFeedbackResponse;
import com.blaybus.blaybusbe.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Operation(summary = "[멘토] 줌 피드백 상세 조회", description = "작성된 줌 피드백의 상세 내용을 조회합니다.(멘토만 조회 가능)")
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

    @Operation(summary = "[멘토] 줌 피드백 목록 조회", description = "멘토가 작성한 줌 피드백 목록을 최신순으로 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "줌 피드백 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = ZoomFeedbackListResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content),
            @ApiResponse(responseCode = "403", description = "접근 권한이 없는 사용자", content = @Content),
    })
    ResponseEntity<Page<ZoomFeedbackListResponse>> getZoomFeedbackList(
            @Parameter(hidden = true) CustomUserDetails user,
            @Parameter(description = "멘티 id", required = true , example = "1") Long menteeId,
            @ParameterObject Pageable pageable
    );

    @Operation(summary = "줌 피드백 삭제", description = "작성한 줌 피드백을 삭제합니다. 작성자 본인만 가능합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "403", description = "본인 피드백이 아님"),
            @ApiResponse(responseCode = "404", description = "피드백을 찾을 수 없음")
    })
    ResponseEntity<Void> deleteZoomFeedback(
            @Parameter(hidden = true) CustomUserDetails user,
            @Parameter(description = "피드백 id", required = true , example = "1") Long feedbackId
    );

    @Operation(summary = "줌 피드백 수정", description = "작성한 줌 피드백을 수정합니다. 작성자 본인만 가능합니다.")
    ResponseEntity<ZoomFeedbackResponse> updateZoomFeedback(
            @Parameter(hidden = true) CustomUserDetails user,
            @Parameter(description = "피드백 id", required = true , example = "1")
            Long feedbackId,
            CreateZoomFeedbackRequest request
    );

    @Operation(summary = "[멘티] 줌 피드백 목록 조회", description = "로그인한 멘티가 받은 줌 피드백 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "줌 피드백 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = ZoomFeedbackListResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content),
            @ApiResponse(responseCode = "403", description = "접근 권한이 없는 사용자", content = @Content),
    })
    ResponseEntity<Page<ZoomFeedbackListResponse>> getMenteeZoomFeedbackList(
            @Parameter(hidden = true) CustomUserDetails user,
            Pageable pageable
    );

    @Operation(summary = "[멘티] 줌 피드백 상세 조회", description = "작성된 줌 피드백의 상세 내용을 조회합니다.(멘티만 조회 가능)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "줌 조회 성공",
                    content = @Content(schema = @Schema(implementation = ZoomFeedbackResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content),
            @ApiResponse(responseCode = "403", description = "접근 권한이 없는 사용자", content = @Content),
    })
    ResponseEntity<ZoomFeedbackResponse> getMenteeZoomFeedback(
            @Parameter(hidden = true) CustomUserDetails user,
            @Parameter(description = "피드백 id", required = true , example = "1")
            @PathVariable Long feedbackId
    );
}
