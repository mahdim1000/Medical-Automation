package com.example.epointment.common;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.*;

@Component
@Entity
@Scope(scopeName = "request")
public class Insurance {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Insu_Seq_Gen")
    @SequenceGenerator(name = "Insu_Seq_Gen", sequenceName = "INSU_SEQ_GEN", allocationSize = 1)
    Long id;
    String name;

    public Long getId() {
        return id;
    }

    public Insurance setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Insurance setName(String name) {
        this.name = name;
        return this;
    }
}
