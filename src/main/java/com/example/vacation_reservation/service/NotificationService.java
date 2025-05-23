//package com.example.vacation_reservation.service;
//
//import com.example.vacation_reservation.dto.notification.NotificationResponse;
//import com.example.vacation_reservation.entity.Notification;
//import com.example.vacation_reservation.entity.User;
//import com.example.vacation_reservation.entity.vacation.Vacation;
//import com.example.vacation_reservation.repository.NotificationRepository;
//import com.example.vacation_reservation.repository.UserRepository;
//import com.example.vacation_reservation.repository.vacation.VacationRepository;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//public class NotificationService {
//
//    private final SimpMessagingTemplate messagingTemplate;
//    private final NotificationRepository notificationRepository;
//    private final UserRepository userRepository;
//    private final VacationRepository vacationRepository;
//
//    public NotificationService(SimpMessagingTemplate messagingTemplate,
//                               NotificationRepository notificationRepository,
//                               UserRepository userRepository,
//                               VacationRepository vacationRepository) {
//        this.messagingTemplate = messagingTemplate;
//        this.notificationRepository = notificationRepository;
//        this.userRepository = userRepository;
//        this.vacationRepository = vacationRepository;
//    }
//
//    // 휴가 상태 변경 시 알림을 보낼 때 호출되는 메서드
//    public void sendVacationStatusUpdateNotification(String employeeId, Long vacationId, String message) {
//        // User와 Vacation을 DB에서 조회
//        User user = userRepository.findByEmployeeId(employeeId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//        Vacation vacation = vacationRepository.findById(vacationId)
//                .orElseThrow(() -> new RuntimeException("Vacation not found"));
//
//        // 알림을 DB에 저장
//        Notification notification = new Notification(user, vacation, message, LocalDateTime.now());
//        notificationRepository.save(notification);
//
//        // WebSocket을 통해 알림 전송
//        messagingTemplate.convertAndSend("/topic/notifications/" + employeeId, message); // 사용자에게 알림 전송
//    }
//
//    // 알림 내역 조회
//    public List<NotificationResponse> getNotificationsByUserId(Long userId) {
//        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
//
//        // Notification 객체들을 NotificationResponse DTO로 변환하여 반환
//        return notifications.stream()
//                .map(notification -> new NotificationResponse(
//                        notification.getMessage(),
//                        notification.getCreatedAt()
//                ))
//                .collect(Collectors.toList());  // 알림이 없으면 빈 리스트 반환
//    }
//
//}
//
//
