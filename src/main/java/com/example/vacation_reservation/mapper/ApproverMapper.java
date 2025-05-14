package com.example.vacation_reservation.mapper;

import com.example.vacation_reservation.dto.approver.ApproverResponseDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ApproverMapper {
    List<ApproverResponseDto> findAvailableApprovers(@Param("positionLevel") int positionLevel, @Param("query") String query);
}
