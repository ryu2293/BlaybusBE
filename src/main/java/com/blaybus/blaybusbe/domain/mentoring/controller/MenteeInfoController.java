package com.blaybus.blaybusbe.domain.mentoring.controller;


import com.blaybus.blaybusbe.domain.mentoring.controller.api.MenteeInfoApi;
import com.blaybus.blaybusbe.domain.mentoring.dto.response.ResponseMenteeInfoDto;
import com.blaybus.blaybusbe.domain.mentoring.dto.response.ResponseMyMentorDto;
import com.blaybus.blaybusbe.domain.mentoring.service.MenteeInfoService;
import com.blaybus.blaybusbe.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MenteeInfoController implements MenteeInfoApi {

    private final MenteeInfoService menteeInfoService;

    /**
     * 멘토와 맵핑된 멘티 목록 조회
     *
     * @param customUserDetails 토큰 추출
     * @param pageable          페이지네이션
     */
    @Override
    @GetMapping("/mentor/mentees")
    public ResponseEntity<Page<ResponseMenteeInfoDto>> findMyMentees(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @ParameterObject Pageable pageable
    ) {

        return ResponseEntity.ok(menteeInfoService.findMyMentees(customUserDetails.getId(), pageable));
    }

    /**
     * 멘티와 매핑된 멘토 조회
     *
     * @param customUserDetails 토큰 추출
     * @return 멘토 정보 리턴
     */
    @Override
    @GetMapping("/mentee/mentor")
    public ResponseEntity<ResponseMyMentorDto> findMyMentor(@AuthenticationPrincipal CustomUserDetails customUserDetails) {

        return ResponseEntity.ok(menteeInfoService.findMyMentor(customUserDetails.getId()));
    }

    @Override
    @DeleteMapping("/mentor/mentee/{menteeId}")
    public ResponseEntity<Void> deleteMenteeInfo(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long menteeId) {

        menteeInfoService.deleteMenteeInfo(customUserDetails.getId(), menteeId);
        return ResponseEntity.noContent().build();
    }
}