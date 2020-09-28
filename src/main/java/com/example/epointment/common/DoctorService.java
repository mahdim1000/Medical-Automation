package com.example.epointment.common;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.*;

@Component
@Entity
@Scope(scopeName = "request")
public class DoctorService {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DocServ_Gen_Seq")
    @SequenceGenerator(name = "DocServ_Gen_Seq", sequenceName = "DOCSERV_GEN_SEQ", allocationSize = 1)
    Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "service_id")
    Service service;

    @ManyToOne( optional = false)
    @JoinColumn(name = "doctor_id")
    Doctor doctor;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }
}
