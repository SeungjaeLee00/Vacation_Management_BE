package com.example.vacation_reservation.controller;

import com.example.vacation_reservation.dto.approver.ApproverResponseDto;
import com.example.vacation_reservation.entity.User;
import com.example.vacation_reservation.exception.CustomException;
import com.example.vacation_reservation.security.CustomUserDetails;
import com.example.vacation_reservation.service.ApproverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/approvers")
@RequiredArgsConstructor
public class ApproverController {

    private final ApproverService approverService;

    @GetMapping
    public ResponseEntity<List<ApproverResponseDto>> getAvailableApprovers(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) String query) {

        if (userDetails == null || userDetails.getUser() == null) {
            throw new CustomException("인증된 사용자가 아닙니다.");
        }

        User currentUser = userDetails.getUser();
        List<ApproverResponseDto> approvers = approverService.findAvailableApprovers(currentUser, query);

        return ResponseEntity.ok(approvers);
    }
}

