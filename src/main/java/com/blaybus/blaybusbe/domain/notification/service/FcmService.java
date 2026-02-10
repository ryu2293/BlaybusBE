package com.blaybus.blaybusbe.domain.notification.service;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class FcmService {

    public void sendPush(String fcmToken, String title, String body, Map<String, String> data) {
        if (fcmToken == null || fcmToken.isBlank()) {
            log.debug("FCM 토큰이 없어 푸시 전송을 건너뜁니다.");
            return;
        }

        if (FirebaseApp.getApps().isEmpty()) {
            log.warn("Firebase가 초기화되지 않아 푸시 전송을 건너뜁니다.");
            return;
        }

        try {
            Message.Builder messageBuilder = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build());

            if (data != null && !data.isEmpty()) {
                messageBuilder.putAllData(data);
            }

            String response = FirebaseMessaging.getInstance().send(messageBuilder.build());
            log.debug("FCM 푸시 전송 성공: {}", response);
        } catch (Exception e) {
            log.error("FCM 푸시 전송 실패: token={}, title={}", fcmToken, title, e);
        }
    }

    // 기존 호환용 오버로드
    public void sendPush(String fcmToken, String title, String body) {
        sendPush(fcmToken, title, body, null);
    }
}
