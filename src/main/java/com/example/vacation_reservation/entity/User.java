package com.example.vacation_reservation.entity;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ojt_User")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_number", nullable = false, length = 10)
    private String employeeId;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(nullable = false, length = 50)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.EMPLOYEE; // 기본값 설정

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public enum Role {
        EMPLOYEE, ADMIN
    }

    @Column(name = "refresh_token", nullable = false, length = 512)
    private String refreshToken;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    // 즉시 로딩됨.. 연관 엔티티가 많으면 좀.. 성능 문제가 있는데.. 하 대규모에서는 별론데
    @ManyToOne(fetch = FetchType.EAGER)  // 사용자 조회가 jpa라서 lazy 발생함.. 그래서 일단 eager로 바꾸긴 했는데 사용자 조회를 mybatis로 바꾸는 걸 생각해야함..
    @JoinColumn(name = "position_id")
    private Position position;

    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    public User(String employeeId, String name, String email, String password, Role role, Position position) {
        this.employeeId = employeeId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.position = position;
    }
}
