package com.example.javaprojet.entity;
import java.util.Date;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
//@Data?
@Getter
@Setter
public abstract class Ressource {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @Column(nullable = false)
    private String nom;

    private String type;

    private String chemin;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreation;



}

