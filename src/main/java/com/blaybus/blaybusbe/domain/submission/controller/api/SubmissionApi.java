package com.blaybus.blaybusbe.domain.submission.controller.api;

import com.blaybus.blaybusbe.domain.submission.dto.request.CreateSubmissionRequest;
import com.blaybus.blaybusbe.domain.submission.dto.response.SubmissionResponse;
import com.blaybus.blaybusbe.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Submission", description = "과제 제출 API")
public interface SubmissionApi {

    @Operation(summary = "과제 제출 등록", description = "과제에 대한 제출물을 등록합니다. 제출 시 Task 상태가 자동으로 DONE으로 변경됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "제출 성공"),
            @ApiResponse(responseCode = "404", description = "과제를 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "이미 제출물이 존재함"),
            @ApiResponse(responseCode = "500", description = "이미지 업로드 실패")
    })
    ResponseEntity<SubmissionResponse> createSubmission(
            @Parameter(hidden = true) CustomUserDetails user,
            @Parameter(description = "과제 ID") Long taskId,
            @Parameter(description = "이미지 파일 리스트") List<MultipartFile> files,
            String menteeComment
    );

    @Operation(summary = "과제 제출물 조회", description = "과제의 제출물을 조회합니다. (접근 권한이 없다면 차단)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "403", description = "접근 권한이 없음."),
            @ApiResponse(responseCode = "404", description = "과제 또는 제출물을 찾을 수 없음")
    })
    ResponseEntity<SubmissionResponse> getSubmission(
            @Parameter(hidden = true) CustomUserDetails user,
            @Parameter(description = "과제 ID") Long taskId
    );

    @Operation(summary = "제출물 삭제", description = "제출물을 삭제합니다. 본인 제출물만 삭제 가능합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "403", description = "삭제 권한 없음"),
            @ApiResponse(responseCode = "404", description = "제출물을 찾을 수 없음")
    })
    ResponseEntity<Void> deleteSubmission(
            @Parameter(hidden = true) CustomUserDetails user,
            @Parameter(description = "제출물 ID") Long submissionId
    );
}
