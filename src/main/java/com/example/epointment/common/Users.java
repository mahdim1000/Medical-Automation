package com.example.epointment.common;

import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.persistence.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Collection;

@Component
@Entity
@Scope(scopeName = "request")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "d_type", discriminatorType = DiscriminatorType.STRING)
public class Users{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userSeqGen")
    @SequenceGenerator(name = "userSeqGen", sequenceName = "USER_SEQ_GEN", allocationSize = 1)
    protected Long id;
    protected String melliCode;
    @Column(name = "name")
    protected String name;
    protected String password;
    protected String phone;
    protected String role;
    protected byte[] image;

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public Users() {
    }

    public Users(Long id, String melliCode, String name, String password, String phone, String role) {
        this.id = id;
        this.melliCode = melliCode;
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMelliCode() {
        return melliCode;
    }

    public void setMelliCode(String melliCode) {
        this.melliCode = melliCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


}
