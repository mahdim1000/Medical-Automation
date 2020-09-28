package com.example.epointment.common;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.*;

@Component
@Entity
@Scope(scopeName = "request")
public class Office{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Office_Gen_seq")
    @SequenceGenerator(name = "Office_Gen_seq", sequenceName = "OFFICE_GEN_SEQ", allocationSize = 1)
    Long id;
    String address;
    String number;
    @ManyToOne(optional = false)
    @JoinColumn(name = "doctor_id")
    Doctor doctor;

    public Office() {
    }

    public Office( String address, String number, Doctor doctor) {
        this.address = address;
        this.number = number;
        this.doctor=doctor;
    }

    public Office(Long id , String address, String number, Doctor doctor) {
        this.address = address;
        this.number = number;
        this.id = id;
        this.doctor=doctor;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }



    public Long getId() {
        return id;
    }

    public Office setId(Long id) {
        this.id = id;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public Office setAddress(String address) {
        this.address = address;
        return this;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public Office setDoctor(Doctor doctor) {
        this.doctor = doctor;
        return this;
    }
}