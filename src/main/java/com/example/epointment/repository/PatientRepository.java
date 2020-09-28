package com.example.epointment.repository;

import com.example.epointment.common.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    public Patient findByMelliCode(String melliCode);
}
