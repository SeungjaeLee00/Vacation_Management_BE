package com.example.vacation_reservation.controller;

//import com.example.vacation_reservation.dto.notification.NotificationRequest;
import com.example.vacation_reservation.dto.notification.NotificationResponse;
import com.example.vacation_reservation.entity.Notification;
import com.example.vacation_reservation.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // 알림 내역 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationResponse>> getNotifications(@PathVariable Long userId) {
        List<NotificationResponse> notifications = notificationService.getNotificationsByUserId(userId);
        return ResponseEntity.ok(notifications); // 알림 내역 반환
    }

//    // 알림을 보내는 서비스 메서드
//    @PostMapping("/send")
//    public ResponseEntity<Void> sendNotification(@RequestBody NotificationRequest request) {
//        notificationService.sendVacationStatusUpdateNotification(
//                request.getUserId(),     // employeeId
//                request.getVacationId(), // vacationId
//                request.getMessage()     // message
//        );
//        return ResponseEntity.ok().build();
//    }

}

