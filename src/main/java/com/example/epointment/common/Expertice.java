package com.example.epointment.common;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.*;

@Component
@Entity
@Scope(scopeName = "request")
public class Expertice {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Expert_Gen_Seq")
    @SequenceGenerator(name = "Expert_Gen_Seq", sequenceName = "EXPERT_GEN_SEQ", allocationSize = 1)
    Long id;
    String name;

    public Long getId() {
        return id;
    }

    public Expertice setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Expertice setName(String name) {
        this.name = name;
        return this;
    }
}
