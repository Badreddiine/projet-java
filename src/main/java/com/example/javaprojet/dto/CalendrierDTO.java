package com.example.javaprojet.dto;

import com.example.javaprojet.entity.Calendrier;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
