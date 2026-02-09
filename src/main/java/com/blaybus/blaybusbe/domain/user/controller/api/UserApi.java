package com.blaybus.blaybusbe.domain.user.controller.api;

import com.blaybus.blaybusbe.domain.user.dto.request.RequestUpdateUserDto;
import com.blaybus.blaybusbe.domain.user.dto.response.ResponseUserDto;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "회원 관련 API", description = "회원 관련(조회, 수정, 탈퇴) 기능 API")
public interface UserApi {

    @Operation(summary = "회원 정보 조회", description = "회원 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 조회 성공",
                    content = @Content(schema = @Schema(implementation = ResponseUserDto.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content)
    })
    ResponseEntity<ResponseUserDto> findUser(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails user
    );

    @Operation(summary = "회원 프로필 업로드", description = "회원 프로필을 업로드합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 프로필 업로드 성공",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content),
            @ApiResponse(responseCode = "500", description = "파일 업로드 실패", content = @Content)
    })
    ResponseEntity<String> uploadImage(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails user,

            @Parameter(description = "업로드할 이미지")
            @RequestPart(value = "file", required = false) MultipartFile file
    );

    @Operation(summary = "회원 정보 수정", description = "")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 프로필 업로드 성공",
                    content = @Content(schema = @Schema(implementation = ResponseUserDto.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content),
    })
    ResponseEntity<ResponseUserDto> updateUser(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails user,

            @Parameter(description = "수정할 값(이름, 닉네임)", required = true)
            @RequestBody RequestUpdateUserDto requestUpdateUserDto
    );
}