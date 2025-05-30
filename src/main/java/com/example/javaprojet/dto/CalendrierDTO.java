package com.example.javaprojet.dto;

import com.example.javaprojet.entity.Calendrier;
import com.example.javaprojet.entity.Evenement;
import com.example.javaprojet.entity.Utilisateur;
import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class CalendrierDTO {

    private Long id;

    private String nom;

    private boolean estPartage;

    private Long proprietaire;

    public CalendrierDTO(Calendrier calendrier) {
        setId(calendrier.getId());
        setNom(calendrier.getNom());
        setEstPartage(calendrier.isEstPartage());
        setProprietaire(calendrier.getProprietaire().getId());
    }
}
