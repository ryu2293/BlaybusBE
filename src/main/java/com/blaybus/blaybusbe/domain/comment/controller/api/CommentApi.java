package com.blaybus.blaybusbe.domain.comment.controller.api;

import com.blaybus.blaybusbe.domain.comment.dto.request.CreateCommentRequest;
import com.blaybus.blaybusbe.domain.comment.dto.request.UpdateCommentRequest;
import com.blaybus.blaybusbe.domain.comment.dto.response.CommentResponse;
import com.blaybus.blaybusbe.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Comment", description = "피드백 댓글 API")
public interface CommentApi {

    @Operation(summary = "댓글 작성", description = "피드백에 텍스트 댓글을 작성합니다. 멘토/멘티 모두 작성 가능합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "댓글 작성 성공"),
            @ApiResponse(responseCode = "404", description = "피드백을 찾을 수 없음")
    })
    ResponseEntity<CommentResponse> createComment(
            @Parameter(hidden = true) CustomUserDetails user,
            @Parameter(description = "피드백 ID") Long feedbackId,
            CreateCommentRequest request
    );

    @Operation(summary = "댓글 목록 조회", description = "피드백에 작성된 댓글 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "피드백을 찾을 수 없음")
    })
    ResponseEntity<List<CommentResponse>> getComments(
            @Parameter(hidden = true) CustomUserDetails user,
            @Parameter(description = "피드백 ID") Long feedbackId
    );

    @Operation(summary = "댓글 수정", description = "댓글을 수정합니다. 본인이 작성한 댓글만 수정 가능합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "403", description = "수정 권한 없음"),
            @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음")
    })
    ResponseEntity<CommentResponse> updateComment(
            @Parameter(hidden = true) CustomUserDetails user,
            @Parameter(description = "댓글 ID") Long commentId,
            UpdateCommentRequest request
    );

    @Operation(summary = "댓글 삭제", description = "댓글을 삭제합니다. 본인이 작성한 댓글만 삭제 가능합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "403", description = "삭제 권한 없음"),
            @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음")
    })
    ResponseEntity<Void> deleteComment(
            @Parameter(hidden = true) CustomUserDetails user,
            @Parameter(description = "댓글 ID") Long commentId
    );
}
