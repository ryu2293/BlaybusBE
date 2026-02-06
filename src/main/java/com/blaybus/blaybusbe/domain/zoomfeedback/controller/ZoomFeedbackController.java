package com.blaybus.blaybusbe.domain.zoomfeedback.controller;

import com.blaybus.blaybusbe.domain.zoomfeedback.controller.api.ZoomFeedbackApi;
import com.blaybus.blaybusbe.domain.zoomfeedback.dto.request.CreateZoomFeedbackRequest;
import com.blaybus.blaybusbe.domain.zoomfeedback.service.ZoomFeedbackService;
import com.blaybus.blaybusbe.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ZoomFeedbackController implements ZoomFeedbackApi {

    private final ZoomFeedbackService zoomFeedbackService;

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
}
