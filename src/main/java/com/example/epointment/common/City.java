package com.example.epointment.common;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.*;

@Component
@Entity
@Scope(scopeName = "request")
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "citySeqGen")
    @SequenceGenerator(name="citySeqGen", sequenceName = "CITY_SEQ_GEN", allocationSize=1)
    Long id;
    String name;

//    @ManyToOne(optional = false)
//    @JoinColumn(name = "province_id")
//    Province province;

//    public Province getProvince() {
//        return province;
//    }
//
//    public void setProvince(Province province) {
//        this.province = province;
//    }

    public Long getId() {
        return id;
    }

    public City setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public City setName(String name) {
        this.name = name;
        return this;
    }
}
