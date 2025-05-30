package com.example.javaprojet.dto;

import com.example.javaprojet.entity.ListeDiffusion;
import com.example.javaprojet.entity.Projet;
import lombok.Data;

@Data
public class ListDiffusionDTO {

    private String description;
    private boolean estSysteme;
    private Projet projet;

    public ListDiffusionDTO(ListeDiffusion listeDiffusion) {
        setDescription(listeDiffusion.getDescription());
        setEstSysteme(listeDiffusion.isEstSysteme());
        setProjet(listeDiffusion.getProjet());
    }
}
