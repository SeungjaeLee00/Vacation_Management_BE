package com.example.vacation_reservation.service;

import com.example.vacation_reservation.dto.approver.ApproverResponseDto;
import com.example.vacation_reservation.entity.User;
import com.example.vacation_reservation.mapper.ApproverMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApproverService {

    private final ApproverMapper approverMapper;

    public List<ApproverResponseDto> findAvailableApprovers(User currentUser, String query) {
        int currentLevel = currentUser.getPosition().getLevel();  // 이건 현재 로그인한 사람의 level, 여기서 lazy 들어감
        return approverMapper.findAvailableApprovers(currentLevel, query);
    }
}
