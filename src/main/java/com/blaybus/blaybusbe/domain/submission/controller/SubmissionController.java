package com.blaybus.blaybusbe.domain.submission.controller;

import com.blaybus.blaybusbe.domain.submission.controller.api.SubmissionApi;
import com.blaybus.blaybusbe.domain.submission.dto.request.CreateSubmissionRequest;
import com.blaybus.blaybusbe.domain.submission.dto.response.SubmissionResponse;
import com.blaybus.blaybusbe.domain.submission.service.SubmissionService;
import com.blaybus.blaybusbe.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class SubmissionController implements SubmissionApi {

    private final SubmissionService submissionService;

    @Override
    @PostMapping("/tasks/{taskId}/submissions")
    public ResponseEntity<SubmissionResponse> createSubmission(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long taskId,
            @Valid @RequestBody CreateSubmissionRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(submissionService.createSubmission(user.getId(), taskId, request));
    }

    @Override
    @GetMapping("/tasks/{taskId}/submissions")
    public ResponseEntity<SubmissionResponse> getSubmission(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long taskId
    ) {
        return ResponseEntity.ok(submissionService.getSubmission(taskId));
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
