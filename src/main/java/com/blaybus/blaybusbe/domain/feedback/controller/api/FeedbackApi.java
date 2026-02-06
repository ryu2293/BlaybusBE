package com.blaybus.blaybusbe.domain.feedback.controller.api;

import com.blaybus.blaybusbe.domain.feedback.dto.request.UpdateFeedbackRequest;
import com.blaybus.blaybusbe.domain.feedback.dto.response.FeedbackResponse;
import com.blaybus.blaybusbe.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Feedback", description = "이미지 좌표 기반 피드백 API")
public interface FeedbackApi {

    @Operation(summary = "피드백 작성", description = "이미지에 좌표 기반 피드백을 작성합니다. 멘토만 작성 가능합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "피드백 작성 성공"),
            @ApiResponse(responseCode = "404", description = "이미지를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "파일 업로드 실패")
    })
    ResponseEntity<FeedbackResponse> createFeedback(
            @Parameter(hidden = true) CustomUserDetails user,
            @Parameter(description = "이미지 ID") Long imageId,
            @Parameter(description = "설명용 이미지") MultipartFile file,
            @Parameter(description = "피드백 내용", required = true, example = "이렇게 하면 더 적절할 것 같아요.") String content,
            @Parameter(description = "피드백 내용", required = true, example = "0.5") Float xPos,
            @Parameter(description = "피드백 내용", required = true, example = "0.5") Float yPos
    );

    @Operation(summary = "이미지별 피드백 목록 조회", description = "해당 이미지에 작성된 피드백 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "이미지를 찾을 수 없음")
    })
    ResponseEntity<List<FeedbackResponse>> getFeedbacks(
            @Parameter(hidden = true) CustomUserDetails user,
            @Parameter(description = "이미지 ID") Long imageId
    );

    @Operation(summary = "피드백 수정", description = "피드백을 수정합니다. 본인이 작성한 피드백만 수정 가능합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "403", description = "수정 권한 없음"),
            @ApiResponse(responseCode = "404", description = "피드백을 찾을 수 없음")
    })
    ResponseEntity<FeedbackResponse> updateFeedback(
            @Parameter(hidden = true) CustomUserDetails user,
            @Parameter(description = "피드백 ID") Long feedbackId,
            UpdateFeedbackRequest request
    );

    @Operation(summary = "피드백 삭제", description = "피드백을 삭제합니다. 본인이 작성한 피드백만 삭제 가능합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "403", description = "삭제 권한 없음"),
            @ApiResponse(responseCode = "404", description = "피드백을 찾을 수 없음")
    })
    ResponseEntity<Void> deleteFeedback(
            @Parameter(hidden = true) CustomUserDetails user,
            @Parameter(description = "피드백 ID") Long feedbackId
    );
}
