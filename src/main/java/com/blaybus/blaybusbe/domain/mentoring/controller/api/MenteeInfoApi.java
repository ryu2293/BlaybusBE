package com.blaybus.blaybusbe.domain.mentoring.controller.api;


import com.blaybus.blaybusbe.domain.mentoring.dto.response.ResponseMenteeInfoDto;
import com.blaybus.blaybusbe.domain.mentoring.dto.response.ResponseMyMentorDto;
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

@Tag(name = "멘토-멘티 API", description = "멘토-멘티 관계(내 멘티 조회, 내 멘토 조회 등) API")
public interface MenteeInfoApi {

    @Operation(summary = "내 멘티 목록 조회", description = "")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ResponseMenteeInfoDto.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content),
            @ApiResponse(responseCode = "403", description = "접근 권한이 없는 사용자", content = @Content),
            @ApiResponse(responseCode = "404", description = "조회 실패", content = @Content)
    })
    ResponseEntity<Page<ResponseMenteeInfoDto>> findMyMentees(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails customUserDetails,

            @ParameterObject Pageable pageable
    );

    @Operation(summary = "내 멘토 조회", description = "")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ResponseMyMentorDto.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content),
            @ApiResponse(responseCode = "403", description = "접근 권한이 없는 사용자", content = @Content),
            @ApiResponse(responseCode = "404", description = "조회 실패", content = @Content)
    })
    ResponseEntity<ResponseMyMentorDto> findMyMentor(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    );
}
