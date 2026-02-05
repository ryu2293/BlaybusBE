package com.blaybus.blaybusbe.domain.studyContent.controller.api;

import com.blaybus.blaybusbe.domain.studyContent.dto.response.ResponseContentDto;
import com.blaybus.blaybusbe.domain.studyContent.enums.Subject;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "학습 자료 관련 API", description = "학습 자료(등록, 삭제 등) API")
public interface StudyContentApi {


    @Operation(summary = "학습 자료 업로드 및 등록", description = "멘토가 PDF 파일과 함께 제목, 과목 정보를 입력하여 학습 자료를 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "학습 자료 등록 성공",
                    content = @Content(schema = @Schema(implementation = Long.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한이 없는 사용자", content = @Content)
    })
    ResponseEntity<Long> uploadStudyContent(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails user,

            @Parameter(description = "학습 자료 제목")
            @RequestPart("title") String title,

            @Parameter(description = "과목 (KOREAN, ENGLISH, MATH 등)")
            @RequestPart("subject") Subject subject,

            @Parameter(description = "업로드할 PDF 파일")
            @RequestPart(value = "file", required = false) MultipartFile file
    );

    @Operation(summary = "학습 자료 목록 조회 (페이징)", description = "멘토가 등록한 학습 자료를 페이지 단위로 조회합니다." +
            "(mentor_id가 null이면 설스터디에서 제공한 공식 자료입니다.)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "학습 자료 조회 성공",
                    content = @Content(schema = @Schema(implementation = ResponseContentDto.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한이 없는 사용자", content = @Content)
    })
    ResponseEntity<Page<ResponseContentDto>> getStudyContents(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails user,

            @ParameterObject Pageable pageable
    );

    @Operation(summary = "학습 자료 삭제", description = "멘토 본인이 등록한 학습 자료를 삭제합니다. (공식 자료는 삭제 불가)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 성공",
                    content = @Content(schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "403", description = "삭제 권한 없음(공식 자료 등)"),
            @ApiResponse(responseCode = "404", description = "자료를 찾을 수 없음")
    })
    ResponseEntity<Void> deleteStudyContent(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long contentId
    );
}
