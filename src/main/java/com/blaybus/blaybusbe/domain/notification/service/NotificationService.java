package com.blaybus.blaybusbe.domain.notification.service;

import com.blaybus.blaybusbe.domain.notification.dto.response.NotificationResponse;
import com.blaybus.blaybusbe.domain.notification.entity.Notification;
import com.blaybus.blaybusbe.domain.notification.enums.NotificationType;
import com.blaybus.blaybusbe.domain.notification.repository.NotificationRepository;
import com.blaybus.blaybusbe.domain.user.entity.User;
import com.blaybus.blaybusbe.domain.user.repository.UserRepository;
import com.blaybus.blaybusbe.global.exception.CustomException;
import com.blaybus.blaybusbe.global.exception.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final FcmService fcmService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void send(NotificationType type, Long recipientUserId, String message,
                     Long targetId, Long feedbackId, Long taskId, Long menteeId) {
        User user = userRepository.findById(recipientUserId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Notification notification = Notification.builder()
                .type(type)
                .message(message)
                .user(user)
                .targetId(targetId)
                .feedbackId(feedbackId)
                .taskId(taskId)
                .menteeId(menteeId)
                .build();
        notificationRepository.save(notification);

        if (Boolean.TRUE.equals(user.getIsAlarmEnabled()) && user.getFcmToken() != null) {
            try {
                fcmService.sendPush(user.getFcmToken(), "설스터디", message,
                        Map.of(
                                "targetType", type.name(),
                                "targetId", targetId != null ? targetId.toString() : "",
                                "feedbackId", feedbackId != null ? feedbackId.toString() : "",
                                "taskId", taskId != null ? taskId.toString() : "",
                                "menteeId", menteeId != null ? menteeId.toString() : ""
                        ));
            } catch (Exception e) {
                log.warn("FCM 푸시 전송 실패: userId={}, message={}", recipientUserId, message, e);
            }
        }
    }

    // 기존 호환용 오버로드
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void send(NotificationType type, Long recipientUserId, String message) {
        send(type, recipientUserId, message, null, null, null, null);
    }

    public Page<NotificationResponse> getNotifications(Long userId, String filter, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Notification> result;

        if ("unread".equals(filter)) {
            result = notificationRepository.findByUserIdAndIsRead(userId, false, pageable);
        } else if ("read".equals(filter)) {
            result = notificationRepository.findByUserIdAndIsRead(userId, true, pageable);
        } else {
            result = notificationRepository.findByUserId(userId, pageable);
        }

        return result.map(NotificationResponse::from);
    }

    @Transactional
    public void markAsRead(Long userId, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND));

        if (!notification.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        notification.markAsRead();
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsRead(userId);
    }

    @Transactional
    public void registerFcmToken(Long userId, String fcmToken) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        user.setFcmToken(fcmToken);
    }
}
