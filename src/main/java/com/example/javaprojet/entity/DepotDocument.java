package com.example.javaprojet.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class DepotDocument extends Ressource {

    private boolean estPublic;

    @ManyToOne
    @JoinColumn(name = "id_projet")
    private Projet projet;


}