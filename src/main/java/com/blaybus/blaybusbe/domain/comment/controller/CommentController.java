package com.blaybus.blaybusbe.domain.comment.controller;

import com.blaybus.blaybusbe.domain.comment.controller.api.CommentApi;
import com.blaybus.blaybusbe.domain.comment.dto.request.CreateCommentRequest;
import com.blaybus.blaybusbe.domain.comment.dto.request.UpdateCommentRequest;
import com.blaybus.blaybusbe.domain.comment.dto.response.CommentResponse;
import com.blaybus.blaybusbe.domain.comment.service.CommentService;
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
public class CommentController implements CommentApi {

    private final CommentService commentService;

    @Override
    @PostMapping("/feedback/{feedbackId}/comments")
    public ResponseEntity<CommentResponse> createComment(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long feedbackId,
            @Valid @RequestBody CreateCommentRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.createComment(user.getId(), feedbackId, request));
    }

    @Override
    @GetMapping("/feedback/{feedbackId}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long feedbackId
    ) {
        return ResponseEntity.ok(commentService.getComments(feedbackId));
    }

    @Override
    @PutMapping("/feedback/comments/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long commentId,
            @Valid @RequestBody UpdateCommentRequest request
    ) {
        return ResponseEntity.ok(commentService.updateComment(user.getId(), commentId, request));
    }

    @Override
    @DeleteMapping("/feedback/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long commentId
    ) {
        commentService.deleteComment(user.getId(), commentId);
        return ResponseEntity.noContent().build();
    }
}
