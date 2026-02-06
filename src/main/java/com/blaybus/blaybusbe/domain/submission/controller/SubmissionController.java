package com.blaybus.blaybusbe.domain.submission.controller;

import com.blaybus.blaybusbe.domain.submission.controller.api.SubmissionApi;
import com.blaybus.blaybusbe.domain.submission.dto.request.CreateSubmissionRequest;
import com.blaybus.blaybusbe.domain.submission.dto.response.SubmissionResponse;
import com.blaybus.blaybusbe.domain.submission.service.SubmissionService;
import com.blaybus.blaybusbe.global.s3.S3Service;
import com.blaybus.blaybusbe.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SubmissionController implements SubmissionApi {

    private final SubmissionService submissionService;
    private final S3Service s3Service;

    /**
     * 멘티가 과제 제출을 합니다.
     *
     * @param user 토큰 추출
     * @param taskId 과제 id
     * @param files 업로드할 파일리스트
     * @param menteeComment 멘티 과제 제출 내용
     */
    @Override
    @PostMapping(value = "/tasks/{taskId}/submissions",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SubmissionResponse> createSubmission(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long taskId,
            @RequestPart(value = "files") List<MultipartFile> files,
            @RequestParam(value = "comment", required = false) String menteeComment
    ) {
        // S3 실제 파일을 업로드 후 URL 리스트 획득.
        List<String> uploadedUrls = files.stream()
                .map(s3Service::uploadAssignmentImage)
                .toList();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(submissionService.createSubmission(user.getId(), taskId, uploadedUrls, menteeComment));
    }

    /**
     * 제출한 과제를 조회합니다.
     *
     * @param user 토큰 추출
     * @param taskId 과제 id
     */
    @Override
    @GetMapping("/mentee/tasks/{taskId}/submissions")
    public ResponseEntity<SubmissionResponse> getSubmission(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long taskId
    ) {
        return ResponseEntity.ok(submissionService.getSubmission(user.getId(), taskId));
    }

    @Override
    @DeleteMapping("/tasks/submissions/{submissionId}")
    public ResponseEntity<Void> deleteSubmission(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long submissionId
    ) {
        submissionService.deleteSubmission(user.getId(), submissionId);
        return ResponseEntity.noContent().build();
    }
}
