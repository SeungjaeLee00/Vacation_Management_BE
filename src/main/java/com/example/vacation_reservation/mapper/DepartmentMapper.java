package com.example.vacation_reservation.mapper;

import com.example.vacation_reservation.dto.department.DepartmentDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DepartmentMapper {
    List<DepartmentDto> findAll();
}
