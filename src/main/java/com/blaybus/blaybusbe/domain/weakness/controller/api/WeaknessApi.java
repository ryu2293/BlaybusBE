package com.blaybus.blaybusbe.domain.weakness.controller.api;

import com.blaybus.blaybusbe.domain.mentoring.dto.response.ResponseMyMentorDto;
import com.blaybus.blaybusbe.domain.weakness.dto.request.RequestWeaknessDto;
import com.blaybus.blaybusbe.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
}
