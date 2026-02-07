package com.blaybus.blaybusbe.domain.feedback.controller;

import com.blaybus.blaybusbe.domain.feedback.controller.api.FeedbackApi;
import com.blaybus.blaybusbe.domain.feedback.dto.request.UpdateFeedbackRequest;
import com.blaybus.blaybusbe.domain.feedback.dto.response.FeedbackListResponse;
import com.blaybus.blaybusbe.domain.feedback.dto.response.FeedbackResponse;
import com.blaybus.blaybusbe.domain.feedback.service.FeedbackService;
import com.blaybus.blaybusbe.domain.task.enums.Subject;
import com.blaybus.blaybusbe.global.s3.S3Service;
import com.blaybus.blaybusbe.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FeedbackController implements FeedbackApi {

    private final FeedbackService feedbackService;
    private final S3Service s3Service;

    /**
     * 이미지에 위치를 찍어서 피드백 합니다.
     *
     * @param user 토큰 추출
     * @param imageId 이미지 id
     * @param file 설명용 이미지
     * @param content 피드백 내용
     * @param xPos
     * @param yPos
     */
    @Override
    @PostMapping(value = "/images/{imageId}/feedback", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FeedbackResponse> createFeedback(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long imageId,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestParam("content") String content,
            @RequestParam("xPos") Float xPos,
            @RequestParam("yPos") Float yPos
    ) {
        String uploadedImageUrl = null;

        // 파일이 전송되었다면 S3에 업로드
        if (file != null && !file.isEmpty()) {
            uploadedImageUrl = s3Service.uploadFeedbackImage(file);
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(feedbackService.createFeedback(user.getId(), imageId, uploadedImageUrl, content, xPos, yPos));
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

    @Override
    @GetMapping("/feedbacks/yesterday")
    public ResponseEntity<Page<FeedbackListResponse>> getYesterdayFeedbacks(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return ResponseEntity.ok(feedbackService.getYesterdayFeedbacks(
                user.getId(), PageRequest.of(page, size)));
    }

    @Override
    @GetMapping("/feedbacks/history")
    public ResponseEntity<Page<FeedbackListResponse>> getFeedbackHistory(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam Long menteeId,
            @RequestParam(required = false) Subject subject,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer weekNumber,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return ResponseEntity.ok(feedbackService.getFeedbackHistory(
                user.getId(), menteeId, subject, year, month, startDate, endDate, PageRequest.of(page, size)));
    }
}
