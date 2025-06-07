package com.example.javaprojet.dto;

import com.example.javaprojet.entity.Calendrier;
import com.example.javaprojet.entity.Utilisateur;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CalendrierDTO {

    private Long id;
    private String nom;

    private boolean estPartage;
    @JsonIgnore
    private Utilisateur proprietaire;


    public CalendrierDTO(Calendrier calendrier) {
        setId(calendrier.getId());
        setNom(calendrier.getNom());
        setEstPartage(calendrier.isEstPartage());
        setProprietaire(calendrier.getProprietaire());
    }
}
