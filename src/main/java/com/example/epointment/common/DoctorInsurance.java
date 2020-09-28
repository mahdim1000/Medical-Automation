package com.example.epointment.common;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.*;

@Component
@Entity
@Scope(scopeName = "request")
@Table(name = "doctor_insurance")
public class DoctorInsurance {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DocIns_Gen_Seq")
    @SequenceGenerator(name = "DocIns_Gen_Seq", sequenceName = "DOCINS_GEN_SEQ", allocationSize = 1)
    Long id;

    @ManyToOne( optional = false)
    @JoinColumn(name = "insurance_id")
    Insurance insurance;

    @ManyToOne(optional = false)
    @JoinColumn(name = "doctor_id")
    Doctor doctor;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public com.example.epointment.common.Insurance getInsurance() {
        return insurance;
    }

    public void setInsurance(com.example.epointment.common.Insurance insurance) {
        this.insurance = insurance;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }
}


