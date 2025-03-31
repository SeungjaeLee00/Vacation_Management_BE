package com.example.vacation_reservation.service;

import com.example.vacation_reservation.entity.Vacation;
import com.example.vacation_reservation.repository.VacationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VacationService {

    @Autowired
    private VacationRepository vacationRepository;

    public void saveVacationRequest(Vacation vacation) {
        vacationRepository.save(vacation);
    }
}
