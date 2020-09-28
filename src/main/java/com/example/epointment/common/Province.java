//package com.example.epointment.common;
//
//import org.springframework.context.annotation.Scope;
//import org.springframework.stereotype.Component;
//
//import javax.persistence.*;
//import java.util.List;
//
//@Component
//@Entity
//@Scope(scopeName = "request")
//public class Province {
//    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Province_Gen_Seq")
//    @SequenceGenerator(name = "Province_Gen_Seq", sequenceName = "PROVINCE_GEN_SEQ", allocationSize = 1)
//    Long id;
//    String name;
//
//    @OneToMany
//    List<City> cityList;
//
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public List<City> getCityList() {
//        return cityList;
//    }
//
//    public void setCityList(List<City> cityList) {
//        this.cityList = cityList;
//    }
//}
