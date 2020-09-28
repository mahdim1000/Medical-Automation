package com.example.epointment.common;

import org.hibernate.annotations.Cascade;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.List;

@Component
@Entity
@Scope(scopeName = "request")
@DiscriminatorValue("doctor")
public class Doctor extends Users implements Cloneable {

    String doctorCode;
    @Column(name = "lastname")
    String lastname;
    String bio;
    Boolean gender;

    @ManyToOne( optional = false)
    @JoinColumn(name = "education_id")
    Education education;

    @OneToMany(mappedBy = "doctor")
    List<DoctorInsurance> insuranceList;

    @OneToMany
    List<DoctorService> serviceList;

    @OneToMany(mappedBy = "doctor")
    List<Turn> turnList;

    @ManyToOne(optional = false)
    @JoinColumn(name = "city_id")
    City city;

    @OneToMany(mappedBy = "doctor")
    List<Office> officeList;

    @OneToMany(mappedBy = "doctor")
    List<ClinicOffice> clinics;

    @ManyToOne(optional = false)
    @JoinColumn(name = "expertice_id")
    Expertice expertice;

    @OneToMany(mappedBy = "doctor")
    List<Patient> myPatients;


    public List<Turn> getTurnList() {
        return turnList;
    }

    public void setTurnList(List<Turn> turnList) {
        this.turnList = turnList;
    }

    public List<Patient> getMyPatients() {
        return myPatients;
    }

    public void setMyPatients(List<Patient> myPatients) {
        this.myPatients = myPatients;
    }

    public Boolean getGender() {
        return gender;
    }

    public void setGender(Boolean gender) {
        this.gender = gender;
    }

    public List<ClinicOffice> getClinics() {
        return clinics;
    }

    public void setClinics(List<ClinicOffice> clinics) {
        this.clinics = clinics;
    }

    public List<Office> getOfficeList() {
        return officeList;
    }

    public void setOfficeList(List<Office> officeList) {
        this.officeList = officeList;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Expertice getExpertice() {
        return expertice;
    }

    public void setExpertice(Expertice expertice) {
        this.expertice = expertice;
    }



    public List<DoctorService> getServiceList() {
        return serviceList;
    }

    public void setServiceList(List<DoctorService> serviceList) {
        this.serviceList = serviceList;
    }

    public List<DoctorInsurance> getInsuranceList() {
        return insuranceList;
    }

    public void setInsuranceList(List<DoctorInsurance> insuranceList) {
        this.insuranceList = insuranceList;
    }

    public Education getEducation() {
        return education;
    }

    public Doctor setEducation(Education education) {
        this.education = education;
        return this;
    }

    public String getDoctorCode() {
        return doctorCode;
    }

    public Doctor setDoctorCode(String doctorCode) {
        this.doctorCode = doctorCode;
        return this;
    }

    public String getLastname() {
        return lastname;
    }

    public Doctor setLastname(String lastname) {
        this.lastname = lastname;
        return this;
    }

    public String getBio() {
        return bio;
    }

    public Doctor setBio(String bio) {
        this.bio = bio;
        return this;
    }

}
