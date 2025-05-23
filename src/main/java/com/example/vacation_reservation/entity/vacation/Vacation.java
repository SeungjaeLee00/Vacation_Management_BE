package com.example.vacation_reservation.entity.vacation;

import com.example.vacation_reservation.entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ojt_Vacation")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vacation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User와 N:1
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "vacation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VacationUsed> usedVacations;

    // 결재자도 User 테이블 참조
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id")
    private User approver;

    @Lob  // text 타입 매핑, varchar, 200자 내외
    @Column(nullable = false)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VacationStatus status;

    @Column(name = "start_at", nullable = false)
    private LocalDate startAt;

    @Column(name = "end_at", nullable = false)
    private LocalDate endAt;

    @Column(name = "created_at", nullable = false)
    private LocalDate RequestDate;

    @Column(name = "updated_at", nullable = false)  // 생성할 때 같이 넣어주세요
    private LocalDateTime updatedAt;

    public Vacation(User user, LocalDate startAt, LocalDate endAt, String reason, LocalDate RequestDate, VacationStatus status) {
        this.user = user;
        this.startAt = startAt;
        this.endAt = endAt;
        this.reason = reason;
        this.RequestDate = RequestDate;
        this.status = status;
    }

}
