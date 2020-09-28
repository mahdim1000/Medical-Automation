package com.example.epointment.common;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.List;

@Component
@Entity
@Scope(scopeName = "request")
@DiscriminatorValue("patient")
public class Patient extends Users{

    String lastname;

    @OneToMany(mappedBy = "patient")
    List<Turn> TurnList;

    @ManyToOne( optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    Doctor doctor;
    Boolean gender;

    public Boolean getGender() {
        return gender;
    }

    public Patient setGender(Boolean gender) {
        this.gender = gender;
        return this;
    }

    public List<Turn> getTurnList() {
        return TurnList;
    }

    public Patient setTurnList(List<Turn> turnList) {
        TurnList = turnList;
        return this;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public Patient setDoctor(Doctor doctor) {
        this.doctor = doctor;
        return this;
    }

    public String getLastname() {
        return lastname;
    }

    public Patient setLastname(String lastname) {
        this.lastname = lastname;
        return this;
    }
}
