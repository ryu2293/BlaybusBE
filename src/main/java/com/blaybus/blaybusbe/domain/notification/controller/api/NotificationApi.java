package com.blaybus.blaybusbe.domain.notification.controller.api;

import com.blaybus.blaybusbe.domain.notification.dto.request.FcmTokenRequest;
import com.blaybus.blaybusbe.domain.notification.dto.response.NotificationResponse;
import com.blaybus.blaybusbe.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

@Tag(name = "Notification", description = "알림 API")
public interface NotificationApi {

    @Operation(summary = "알림 목록 조회", description = "로그인 사용자의 알림 목록을 조회합니다. filter: all(기본), unread, read")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    ResponseEntity<Page<NotificationResponse>> getNotifications(
            @Parameter(hidden = true) CustomUserDetails user,
            @Parameter(description = "필터 (all / unread / read)") String filter,
            @Parameter(description = "페이지 번호 (0부터 시작)") int page,
            @Parameter(description = "페이지 크기") int size
    );

    @Operation(summary = "알림 읽음 처리", description = "단건 알림을 읽음 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "읽음 처리 성공"),
            @ApiResponse(responseCode = "404", description = "알림을 찾을 수 없음")
    })
    ResponseEntity<Void> markAsRead(
            @Parameter(hidden = true) CustomUserDetails user,
            @Parameter(description = "알림 ID") Long notificationId
    );

    @Operation(summary = "전체 알림 읽음 처리", description = "모든 미읽음 알림을 읽음 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "전체 읽음 처리 성공")
    })
    ResponseEntity<Void> markAllAsRead(
            @Parameter(hidden = true) CustomUserDetails user
    );

    @Operation(summary = "FCM 토큰 등록/갱신", description = "푸시 알림을 위한 FCM 토큰을 등록하거나 갱신합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰 등록 성공")
    })
    ResponseEntity<Void> registerFcmToken(
            @Parameter(hidden = true) CustomUserDetails user,
            FcmTokenRequest request
    );
}
