package com.example.epointment.repository;

import com.example.epointment.common.Doctor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.print.Doc;

@Repository
public interface DoctorRepository extends CrudRepository<Doctor, Long> {
    public Doctor findByMelliCode(String mc);
    public Doctor findByDoctorCode(String dc);
}
