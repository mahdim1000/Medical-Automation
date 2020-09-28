package com.example.epointment.common;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.*;

@Component
@Entity(name="turn_files")
@Table(name = "turn_files")
@Scope(scopeName = "request")
public class TurnFiles {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TurnFiles_Gen_seq")
    @SequenceGenerator(name = "TurnFiles_Gen_seq", sequenceName = "TURNFILES_GEN_SEQ", allocationSize = 1)
    Long id;
    @Column(name = "name")
    String name;
    @ManyToOne(optional = false)
    @JoinColumn(name = "turn_id")
    Turn turn;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Turn getTurn() {
        return turn;
    }

    public void setTurn(Turn turn) {
        this.turn = turn;
    }
}
