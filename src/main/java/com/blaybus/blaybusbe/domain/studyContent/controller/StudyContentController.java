package com.blaybus.blaybusbe.domain.studyContent.controller;

import com.blaybus.blaybusbe.domain.studyContent.controller.api.StudyContentApi;
import com.blaybus.blaybusbe.domain.studyContent.enums.Subject;
import com.blaybus.blaybusbe.domain.studyContent.service.StudyContentService;
import com.blaybus.blaybusbe.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
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
     * @param user 토큰
     * @param title 제목
     * @param subject 과목
     * @param file 학습자료.PDF
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
}
