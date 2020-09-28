package com.example.epointment.common;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Component
@Entity
@Scope(scopeName = "request")
public class TurnService {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TurnServ_Gen_seq")
    @SequenceGenerator(name = "TurnServ_Gen_seq", sequenceName = "TURNSERV_GEN_SEQ", allocationSize = 1)
    Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "turn_id")
    Turn turn;

    @ManyToOne(optional = false)
    @JoinColumn(name = "service_id")
    Service service;

    public Long getId() {
        return id;
    }

    public TurnService setId(Long id) {
        this.id = id;
        return this;
    }

    public Turn getTurn() {
        return turn;
    }

    public TurnService setTurn(Turn turn) {
        this.turn = turn;
        return this;
    }

    public Service getService() {
        return service;
    }

    public TurnService setService(Service service) {
        this.service = service;
        return this;
    }
}
