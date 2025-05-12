package com.example.vacation_reservation.service;

import com.example.vacation_reservation.dto.department.DepartmentDto;
import com.example.vacation_reservation.mapper.DepartmentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentMapper departmentMapper;

    public List<DepartmentDto> getAllDepartments() {
        return departmentMapper.findAll();
    }
}
