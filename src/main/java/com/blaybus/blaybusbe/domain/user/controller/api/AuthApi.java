package com.blaybus.blaybusbe.domain.user.controller.api;

import com.blaybus.blaybusbe.global.security.LoginFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Authentication API", description = "인증 관련 API (로그인, 로그아웃)")
public interface AuthApi {

    @Operation(summary = "로그인", description = "아이디와 비밀번호를 입력하여 로그인합니다. " +
            "(성공 시 Authorization: 'Bearer AccessToken' 발급)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공 (Authorization Header 확인)", content = @Content),
            @ApiResponse(responseCode = "401", description = "로그인 실패 (아이디 또는 비밀번호 불일치)", content = @Content)
    })
    void login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "로그인 정보", required = true)
            @RequestBody LoginFilter.LoginDto loginDto
    );
}