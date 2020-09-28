package com.example.epointment.common;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.*;

@Component
@Entity
@Scope(scopeName = "request")
public class ClinicOffice {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ClinicOff_Gen_seq")
    @SequenceGenerator(name = "ClinicOff_Gen_seq", sequenceName = "CLINICOFF_GEN_SEQ", allocationSize = 1)
    Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "doctor_id", referencedColumnName = "id")
    Doctor doctor;

    @ManyToOne(optional = false)
    @JoinColumn(name = "clinic_id", referencedColumnName = "id")
    Clinic clinic;
    String phone;
    @Column(name = "is_enable")
    Boolean isEnable;

    public Boolean getEnable() {
        return isEnable;
    }

    public void setEnable(Boolean enable) {
        isEnable = enable;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Long getId() {
        return id;
    }

    public ClinicOffice setId(Long id) {
        this.id = id;
        return this;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public ClinicOffice setDoctor(Doctor doctor) {
        this.doctor = doctor;
        return this;
    }

    public Clinic getClinic() {
        return clinic;
    }

    public ClinicOffice setClinic(Clinic clinic) {
        this.clinic = clinic;
        return this;
    }
}
