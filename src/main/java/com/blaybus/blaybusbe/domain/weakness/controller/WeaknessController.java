package com.blaybus.blaybusbe.domain.weakness.controller;

import com.blaybus.blaybusbe.domain.weakness.controller.api.WeaknessApi;
import com.blaybus.blaybusbe.domain.weakness.dto.request.RequestWeaknessDto;
import com.blaybus.blaybusbe.domain.weakness.dto.response.ResponseWeaknessDto;
import com.blaybus.blaybusbe.domain.weakness.service.WeaknessService;
import com.blaybus.blaybusbe.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class WeaknessController implements WeaknessApi {

    private final WeaknessService weaknessService;

    /**
     * 멘토가 멘티의 보완점을 등록합니다.
     *
     * @param customUserDetails 토큰 추출
     * @param request 제목, 멘티 id, 학습자료 id
     * @return
     */
    @Override
    @PostMapping("/mentor/weakness")
    public ResponseEntity<Long> createWeakness(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,

            @RequestBody @Valid RequestWeaknessDto request
    ) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(weaknessService.createWeakness(customUserDetails.getId(), request));
    }

    /**
     * 멘토가 멘티의 보완점 목록 조회
     *
     * @param user 토큰 추출
     * @param menteeId 멘티 id
     * @param pageable 페이지네이션
     */
    @Override
    @GetMapping("/mentor/weakness/{menteeId}")
    public ResponseEntity<Page<ResponseWeaknessDto>> getMenteeWeaknesses(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long menteeId,
            @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.ok(weaknessService.getMenteeWeaknesses(user.getId(), menteeId, pageable));
    }
}
