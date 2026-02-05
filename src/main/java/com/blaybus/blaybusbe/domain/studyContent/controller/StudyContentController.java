package com.blaybus.blaybusbe.domain.studyContent.controller;

import com.blaybus.blaybusbe.domain.studyContent.controller.api.StudyContentApi;
import com.blaybus.blaybusbe.domain.studyContent.dto.response.ResponseContentDto;
import com.blaybus.blaybusbe.domain.studyContent.enums.Subject;
import com.blaybus.blaybusbe.domain.studyContent.service.StudyContentService;
import com.blaybus.blaybusbe.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/study-contents")
public class StudyContentController implements StudyContentApi {

    private final StudyContentService studyContentService;

    /**
     * 학습자료를 저장합니다
     *
     * @param user    토큰
     * @param title   제목
     * @param subject 과목
     * @param file    학습자료.PDF
     */
    @Override
    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> uploadStudyContent(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam("title") String title,
            @RequestParam("subject") Subject subject,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        Long contentId = studyContentService.createStudyContent(user.getId(), title, subject, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(contentId);
    }

    /**
     * 멘토가 등록한 학습자료 목록 조회(mentor_id가 null이면 설스터디측에서 제공한 공식 자료임)
     *
     * @param user     토큰 추출
     * @param pageable 페이지네이션
     */
    @Override
    @GetMapping("")
    public ResponseEntity<Page<ResponseContentDto>> getStudyContents(
            @AuthenticationPrincipal CustomUserDetails user,
            Pageable pageable
    ) {
        return ResponseEntity.ok(studyContentService.getStudyContents(user.getId(), pageable));
    }

    /**
     * 등록한 학습 자료를 삭제합니다.
     *
     * @param user      토큰
     * @param contentId 삭제할 학습자료 id
     */
    @Override
    @DeleteMapping("/{contentId}")
    public ResponseEntity<Void> deleteStudyContent(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long contentId
    ) {
        studyContentService.deleteStudyContent(user.getId(), contentId);
        return ResponseEntity.noContent().build();
    }
}
