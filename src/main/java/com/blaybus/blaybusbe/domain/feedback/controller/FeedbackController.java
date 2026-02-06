package com.blaybus.blaybusbe.domain.feedback.controller;

import com.blaybus.blaybusbe.domain.feedback.controller.api.FeedbackApi;
import com.blaybus.blaybusbe.domain.feedback.dto.request.CreateFeedbackRequest;
import com.blaybus.blaybusbe.domain.feedback.dto.request.UpdateFeedbackRequest;
import com.blaybus.blaybusbe.domain.feedback.dto.response.FeedbackResponse;
import com.blaybus.blaybusbe.domain.feedback.service.FeedbackService;
import com.blaybus.blaybusbe.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FeedbackController implements FeedbackApi {

    private final FeedbackService feedbackService;

    @Override
    @PostMapping("/images/{imageId}/feedback")
    public ResponseEntity<FeedbackResponse> createFeedback(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long imageId,
            @Valid @RequestBody CreateFeedbackRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(feedbackService.createFeedback(user.getId(), imageId, request));
    }

    @Override
    @GetMapping("/images/{imageId}/feedback")
    public ResponseEntity<List<FeedbackResponse>> getFeedbacks(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long imageId
    ) {
        return ResponseEntity.ok(feedbackService.getFeedbacksByImageId(imageId));
    }

    @Override
    @PutMapping("/feedback/{feedbackId}")
    public ResponseEntity<FeedbackResponse> updateFeedback(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long feedbackId,
            @Valid @RequestBody UpdateFeedbackRequest request
    ) {
        return ResponseEntity.ok(feedbackService.updateFeedback(user.getId(), feedbackId, request));
    }

    @Override
    @DeleteMapping("/feedback/{feedbackId}")
    public ResponseEntity<Void> deleteFeedback(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long feedbackId
    ) {
        feedbackService.deleteFeedback(user.getId(), feedbackId);
        return ResponseEntity.noContent().build();
    }
}
