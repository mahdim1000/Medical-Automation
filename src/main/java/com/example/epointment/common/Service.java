package com.example.epointment.common;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.List;

@Component
@Entity
@Scope(scopeName = "request")
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Serv_Gen_Seq")
    @SequenceGenerator(name = "Serv_Gen_Seq", sequenceName = "SERV_GEN_SEQ", allocationSize = 1)
    Long id;
    String name;
    @OneToMany(mappedBy = "service")
    List<TurnService> turnServices;

    public Long getId() {
        return id;
    }

    public Service setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Service setName(String name) {
        this.name = name;
        return this;
    }

    public List<TurnService> getTurnServices() {
        return turnServices;
    }

    public void setTurnServices(List<TurnService> turnServices) {
        this.turnServices = turnServices;
    }
}
