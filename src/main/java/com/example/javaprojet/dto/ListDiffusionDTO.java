package com.example.javaprojet.dto;

import com.example.javaprojet.entity.ListeDiffusion;
import com.example.javaprojet.entity.Projet;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Data
@Getter
@Setter
public class ListDiffusionDTO {
    private String type;
    private String chemin;
    private String nom;
    private Long id ;
    private String description;
    private boolean estSysteme;
    private Projet projet;
    private Date dateCreation;
    public ListDiffusionDTO(ListeDiffusion listeDiffusion) {
        setId(listeDiffusion.getId());
        setType(listeDiffusion.getType());
        setChemin(listeDiffusion.getChemin());
        setNom(listeDiffusion.getNom());
        setDescription(listeDiffusion.getDescription());
        setDateCreation(listeDiffusion.getDateCreation());
        setEstSysteme(listeDiffusion.isEstSysteme());
        setProjet(listeDiffusion.getProjet());
    }

}
