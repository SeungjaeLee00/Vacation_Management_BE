package com.example.vacation_reservation.entity;

import com.example.vacation_reservation.entity.vacation.Vacation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ojt_Notification")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vacation_id")
    private Vacation vacation;

    @Column(nullable = false, length = 255)
    private String message;

    @Column(nullable = false)
    private boolean isRead = false;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public Notification(User user, Vacation vacation, String message, LocalDateTime createdAt) {
        this.user = user;
        this.vacation = vacation;
        this.message = message;
        this.createdAt = createdAt;
    }
}

