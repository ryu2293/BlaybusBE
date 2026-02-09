package com.blaybus.blaybusbe.domain.notification.controller;

import com.blaybus.blaybusbe.domain.notification.controller.api.NotificationApi;
import com.blaybus.blaybusbe.domain.notification.dto.request.FcmTokenRequest;
import com.blaybus.blaybusbe.domain.notification.dto.response.NotificationResponse;
import com.blaybus.blaybusbe.domain.notification.service.NotificationService;
import com.blaybus.blaybusbe.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class NotificationController implements NotificationApi {

    private final NotificationService notificationService;

    @Override
    @GetMapping("/notifications")
    public ResponseEntity<Page<NotificationResponse>> getNotifications(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(defaultValue = "all") String filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(notificationService.getNotifications(user.getId(), filter, page, size));
    }

    @Override
    @PatchMapping("/notifications/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long notificationId
    ) {
        notificationService.markAsRead(user.getId(), notificationId);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PatchMapping("/notifications/read-all")
    public ResponseEntity<Void> markAllAsRead(
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        notificationService.markAllAsRead(user.getId());
        return ResponseEntity.noContent().build();
    }

    @Override
    @PostMapping("/users/me/fcm-token")
    public ResponseEntity<Void> registerFcmToken(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody FcmTokenRequest request
    ) {
        notificationService.registerFcmToken(user.getId(), request.getFcmToken());
        return ResponseEntity.ok().build();
    }
}
