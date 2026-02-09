package com.blaybus.blaybusbe.domain.weakness.controller.api;

import com.blaybus.blaybusbe.domain.mentoring.dto.response.ResponseMyMentorDto;
import com.blaybus.blaybusbe.domain.weakness.dto.request.RequestWeaknessDto;
import com.blaybus.blaybusbe.domain.weakness.dto.response.ResponseWeaknessDto;
import com.blaybus.blaybusbe.domain.weakness.enums.Subject;
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
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "약점 관리 API", description = "멘티의 약점 등록 및 매핑 API")
public interface WeaknessApi {

    @Operation(summary = "멘티 보완점 등록", description = "멘토가 멘티의 보완점을 등록하고 관련 학습 자료를 매핑합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "보완점 등록 성공",
                    content = @Content(schema = @Schema(implementation = Long.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content),
            @ApiResponse(responseCode = "403", description = "접근 권한이 없는 사용자", content = @Content),
    })
    ResponseEntity<Long> createWeakness(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails customUserDetails,

            @Parameter(description = "약점 등록", required = true)
            @RequestBody RequestWeaknessDto request
    );

    @Operation(summary = "멘티의 보완점 목록 조회", description = "특정 멘티에게 등록된 보완점 리스트를 과목별로(과목 미입력 시 전체) 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "멘티 보완점 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = ResponseWeaknessDto.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content),
            @ApiResponse(responseCode = "403", description = "접근 권한이 없는 사용자", content = @Content),
            @ApiResponse(responseCode = "404", description = "검색 실패", content = @Content)
    })
    ResponseEntity<Page<ResponseWeaknessDto>> getMenteeWeaknesses(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails user,

            @Parameter(description = "멘티 id", required = true , example = "1")
            @PathVariable Long menteeId,

            @Parameter(description = "과목 (KOREAN, MATH, ENGLISH)", example = "KOREAN")
            Subject subject,

            @ParameterObject Pageable pageable
    );

    @Operation(summary = "멘티 본인의 약점 목록 조회 (페이징)", description = "로그인한 멘티가 자신에게 등록된 약점(보완점) 리스트를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "멘티 보완점 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = ResponseWeaknessDto.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content),
            @ApiResponse(responseCode = "403", description = "접근 권한이 없는 사용자", content = @Content),
            @ApiResponse(responseCode = "404", description = "검색 실패", content = @Content)
    })
    ResponseEntity<Page<ResponseWeaknessDto>> getMyWeaknesses(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails user,

            @Parameter(description = "과목 (KOREAN, MATH, ENGLISH)", example = "KOREAN")
            Subject subject,
            @ParameterObject Pageable pageable
    );

    @Operation(summary = "약점 삭제", description = "멘토가 등록한 약점을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "멘티 보완점 삭제 성공",
                    content = @Content(schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content),
            @ApiResponse(responseCode = "403", description = "접근 권한이 없는 사용자", content = @Content),
            @ApiResponse(responseCode = "404", description = "검색 실패", content = @Content)
    })
    ResponseEntity<Void> deleteWeakness(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails user,

            @Parameter(description = "약점 id", required = true , example = "1")
            @PathVariable Long weaknessId
    );
}
