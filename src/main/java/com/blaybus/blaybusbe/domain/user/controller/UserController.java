package com.blaybus.blaybusbe.domain.user.controller;

import com.blaybus.blaybusbe.domain.user.controller.api.UserApi;
import com.blaybus.blaybusbe.domain.user.dto.response.ResponseUserDto;
import com.blaybus.blaybusbe.domain.user.service.UserService;
import com.blaybus.blaybusbe.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {

    private final UserService userService;

    /**
     * 로그인 중인 회원 정보 조회
     * @param customUserDetails
     */
    @GetMapping("/users/me")
    public ResponseEntity<ResponseUserDto> findUser(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {

        return ResponseEntity.ok(userService.findUser(customUserDetails.getId()));
    }
}
