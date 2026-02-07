package com.blaybus.blaybusbe.domain.feedback.controller.api;

import com.blaybus.blaybusbe.domain.feedback.dto.request.UpdateFeedbackRequest;
import com.blaybus.blaybusbe.domain.feedback.dto.response.FeedbackListResponse;
import com.blaybus.blaybusbe.domain.feedback.dto.response.FeedbackResponse;
import com.blaybus.blaybusbe.domain.task.enums.Subject;
import com.blaybus.blaybusbe.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
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

    @Operation(summary = "어제자 피드백 목록 조회", description = "어제 생성된 피드백 목록을 페이징 조회합니다. 멘티 본인만 조회 가능합니다. 정렬은 최신순(createdAt DESC) 고정입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    ResponseEntity<Page<FeedbackListResponse>> getYesterdayFeedbacks(
            @Parameter(hidden = true) CustomUserDetails user,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") Integer page,
            @Parameter(description = "페이지 크기", example = "10") Integer size
    );

    @Operation(summary = "이전 피드백 모아보기",
            description = "피드백 생성일 기준으로 과목, 년도, 월, 주차, 시작일, 종료일로 필터링하여 이전 피드백을 페이징 조회합니다. " +
                    "프론트에서 주차 선택 시 startDate/endDate로 날짜 범위를 전달하고, weekNumber는 표시용으로 함께 전달합니다. " +
                    "menteeId == 본인 ID면 멘티 본인 조회, 다른 ID면 멘토-멘티 매핑 검증 후 조회. " +
                    "정렬은 최신순(createdAt DESC) 고정입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "403", description = "멘토-멘티 매핑이 존재하지 않음")
    })
    ResponseEntity<Page<FeedbackListResponse>> getFeedbackHistory(
            @Parameter(hidden = true) CustomUserDetails user,
            @Parameter(description = "멘티 ID (멘티 본인 ID 또는 멘토가 조회할 멘티 ID)", required = true) Long menteeId,
            @Parameter(description = "과목 필터") Subject subject,
            @Parameter(description = "년도 필터", example = "2026") Integer year,
            @Parameter(description = "월 필터 (1~12)", example = "2") Integer month,
            @Parameter(description = "주차 (프론트 표시용, 필터는 startDate/endDate로 적용)", example = "1") Integer weekNumber,
            @Parameter(description = "시작일", example = "2026-02-01") LocalDate startDate,
            @Parameter(description = "종료일", example = "2026-02-07") LocalDate endDate,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") Integer page,
            @Parameter(description = "페이지 크기", example = "10") Integer size
    );
}
