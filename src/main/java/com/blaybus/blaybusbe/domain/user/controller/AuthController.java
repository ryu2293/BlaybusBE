package com.blaybus.blaybusbe.domain.user.controller;

import com.blaybus.blaybusbe.domain.user.controller.api.AuthApi;
import com.blaybus.blaybusbe.global.security.LoginFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    /**
     * 로그인 기능
     * @param loginDto: username(아이디), password(비밀번호)
     */
    @PostMapping("/auth/login")
    public void login(@RequestBody LoginFilter.LoginDto loginDto) {}
}
