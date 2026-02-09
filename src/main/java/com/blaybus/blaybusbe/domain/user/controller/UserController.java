package com.blaybus.blaybusbe.domain.user.controller;

import com.blaybus.blaybusbe.domain.user.controller.api.UserApi;
import com.blaybus.blaybusbe.domain.user.dto.request.RequestUpdateUserDto;
import com.blaybus.blaybusbe.domain.user.dto.response.ResponseUserDto;
import com.blaybus.blaybusbe.domain.user.service.UserService;
import com.blaybus.blaybusbe.global.s3.S3Service;
import com.blaybus.blaybusbe.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController implements UserApi {

    private final UserService userService;
    private final S3Service s3Service;

    /**
     * 로그인 중인 회원 정보 조회
     *
     * @param customUserDetails
     */
    @GetMapping("me")
    public ResponseEntity<ResponseUserDto> findUser(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {

        return ResponseEntity.ok(userService.findUser(customUserDetails.getId()));
    }

    /**
     * 회원 프로필 업데이트
     *
     * @param user 토큰
     * @param file 업로드할 이미지
     * @return 업로드된 이미지 url
     */
    @Override
    @PatchMapping(value = "/me/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadImage(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {

        String imageUrl = null;

        if (file != null && !file.isEmpty()) {
            imageUrl = s3Service.uploadProfileImage(file);
        }

        return ResponseEntity.ok(userService.updateProfileImage(user.getId(), imageUrl));
    }

    /**
     * 회원 정보를 변경합니다
     *
     * @param user 토큰 추출
     * @param requestUpdateUserDto 유저 정보 변경(이름, 닉네임)
     */
    @Override
    @PatchMapping("/me")
    public ResponseEntity<ResponseUserDto> updateUser(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody RequestUpdateUserDto requestUpdateUserDto) {

        return ResponseEntity.ok(userService.updateUser(user.getId(), requestUpdateUserDto));
    }
}
