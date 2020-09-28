package com.example.epointment.common;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.List;

@Component
@Entity
@Scope(scopeName = "request")
@DiscriminatorValue("clinic")
public class Clinic extends Users {


    String clinicNumber;
    String address;
    String bio;
    String clinicPhone;

    @ManyToOne( optional = false)
    @JoinColumn(name = "city_id")
    City city;
    @OneToMany(mappedBy = "clinic")
    List<ClinicOffice> doctors;
    String name;


//    @ManyToMany(mappedBy = "clinics")
//    List<Doctor> doctors;
//
//    public List<Doctor> getDoctors() {
//        return doctors;
//    }
//
//    public void setDoctors(List<Doctor> doctors) {
//        this.doctors = doctors;
//    }


    public String getClinicPhone() {
        return clinicPhone;
    }

    public void setClinicPhone(String clinicPhone) {
        this.clinicPhone = clinicPhone;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public List<ClinicOffice> getDoctors() {
        return doctors;
    }

    public Clinic setDoctors(List<ClinicOffice> doctors) {
        this.doctors = doctors;
        return this;
    }


    public City getCity() {
        return city;
    }

    public Clinic setCity(City city) {
        this.city = city;
        return this;
    }

    public String getClinicNumber() {
        return clinicNumber;
    }

    public Clinic setClinicNumber(String clinicNumber) {
        this.clinicNumber = clinicNumber;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public Clinic setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getBio() {
        return bio;
    }

    public Clinic setBio(String bio) {
        this.bio = bio;
        return this;
    }
}
