package com.example.epointment.common;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import javax.print.Doc;
import java.util.List;

@Component
@Entity
@Scope(scopeName = "request")
public class Education {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Educat_Gen_seq")
    @SequenceGenerator(name = "Educat_Gen_seq", sequenceName = "EDUCATE_GEN_SEQ", allocationSize = 1)
    Long id;
    String name;

    public Long getId() {
        return id;
    }

    public Education setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Education setName(String name) {
        this.name = name;
        return this;
    }

}
