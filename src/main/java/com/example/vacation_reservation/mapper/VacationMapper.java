package com.example.vacation_reservation.mapper;

import com.example.vacation_reservation.dto.vacation.VacationInfoDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface VacationMapper {
    List<VacationInfoDto> findVacationsByDepartmentId(@Param("departmentId") Long departmentId);
}
