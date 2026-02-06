package com.blaybus.blaybusbe.domain.zoomfeedback.controller;

import com.blaybus.blaybusbe.domain.zoomfeedback.controller.api.ZoomFeedbackApi;
import com.blaybus.blaybusbe.domain.zoomfeedback.dto.request.CreateZoomFeedbackRequest;
import com.blaybus.blaybusbe.domain.zoomfeedback.dto.response.ZoomFeedbackListResponse;
import com.blaybus.blaybusbe.domain.zoomfeedback.dto.response.ZoomFeedbackResponse;
import com.blaybus.blaybusbe.domain.zoomfeedback.service.ZoomFeedbackService;
import com.blaybus.blaybusbe.global.security.CustomUserDetails;
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
public class ZoomFeedbackController implements ZoomFeedbackApi {

    private final ZoomFeedbackService zoomFeedbackService;

    /**
     * 멘토가 줌 피드백을 작성합니다.
     *
     * @param user 토큰 추출
     * @param menteeId 멘토 id
     * @param request
     */
    @Override
    @PostMapping("/mentor/zoom-feedback/{menteeId}")
    public ResponseEntity<Long> createZoomFeedback(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long menteeId,
            @RequestBody CreateZoomFeedbackRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(zoomFeedbackService.createZoomFeedback(user.getId(), menteeId, request));
    }

    @Override
    @GetMapping("zoom-feedback/{feedbackId}")
    public ResponseEntity<ZoomFeedbackResponse> getZoomFeedback(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long feedbackId
    ) {
        return ResponseEntity.ok(zoomFeedbackService.getZoomFeedback(user.getId(), feedbackId));
    }

    @Override
    @GetMapping("/mentor/list/{menteeId}")
    public ResponseEntity<Page<ZoomFeedbackListResponse>> getZoomFeedbackList(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long menteeId,
            @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.ok(zoomFeedbackService.getZoomFeedbackList(user.getId(), menteeId, pageable));
    }

    @Override
    @DeleteMapping("/zoom-feedback/{feedbackId}")
    public ResponseEntity<Void> deleteZoomFeedback(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long feedbackId
    ) {
        zoomFeedbackService.deleteZoomFeedback(user.getId(), feedbackId);
        return ResponseEntity.noContent().build();
    }
}
