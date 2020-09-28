package com.example.epointment.common;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.*;

import java.sql.Time;
import java.util.Date;
import java.util.List;

@Component
@Entity
@Scope(scopeName = "request")
public class Turn  {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Turn_Gen_seq")
    @SequenceGenerator(name = "Turn_Gen_seq", sequenceName = "TURN_GEN_SEQ", allocationSize = 1)
    Long id;
    int status;//0 waiting  1 visited  2 canceled
    String comment;
    Double cost;
    Date date;
    @Column(name = "start_time")
    Time startTime;
    @Column(name = "end_time")
    Time endTime;

    @ManyToOne(optional = false)
    @JoinColumn(name = "patient_id")
    Patient patient;


    @ManyToOne(optional = false)
    @JoinColumn(name = "clinic_office_id")
    ClinicOffice clinicOffice;

    @ManyToOne(optional = false)
    @JoinColumn(name = "doctor_id")
    Doctor doctor;

    @ManyToOne(optional = false)
    @JoinColumn(name = "office_id")
    Office office;

    @ManyToOne(optional = false)
    @JoinColumn(name = "insurance_id")
    Insurance insurance;

    @OneToMany(mappedBy = "turn")
    List<TurnService> turnServices;

    @OneToMany(mappedBy = "turn")
    List<TurnFiles> files;

    public List<TurnFiles> getFiles() {
        return files;
    }

    public void setFiles(List<TurnFiles> files) {
        this.files = files;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public Turn setDoctor(Doctor doctor) {
        this.doctor = doctor;
        return this;
    }

    public String getComment() {
        return comment;
    }

    public Turn setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public Double getCost() {
        return cost;
    }

    public Turn setCost(Double cost) {
        this.cost = cost;
        return this;
    }

    public Date getDate() {
        return date;
    }

    public Turn setDate(Date date) {
        this.date = date;
        return this;
    }

    public Time getStartTime() {
        return startTime;
    }

    public Turn setStartTime(Time startTime) {
        this.startTime = startTime;
        return this;
    }

    public Time getEndTime() {
        return endTime;
    }

    public Turn setEndTime(Time endTime) {
        this.endTime = endTime;
        return this;
    }

    public int getStatus() {
        return status;
    }

    public Turn setStatus(int status) {
        this.status = status;
        return this;
    }

    public ClinicOffice getClinicOffice() {
        return clinicOffice;
    }

    public Turn setClinicOffice(ClinicOffice clinicOffice) {
        this.clinicOffice = clinicOffice;
        return this;
    }

    public Office getOffice() {
        return office;
    }

    public Turn setOffice(Office office) {
        this.office = office;
        return this;
    }

    public Insurance getInsurance() {
        return insurance;
    }

    public Turn setInsurance(Insurance insurance) {
        this.insurance = insurance;
        return this;
    }

    public Long getId() {
        return id;
    }

    public Turn setId(Long id) {
        this.id = id;
        return this;
    }

    public Patient getPatient() {
        return patient;
    }

    public Turn setPatient(Patient patient) {
        this.patient = patient;
        return this;
    }

    public List<TurnService> getTurnServices() {
        return turnServices;
    }

    public void setTurnServices(List<TurnService> turnServices) {
        this.turnServices = turnServices;
    }
}
