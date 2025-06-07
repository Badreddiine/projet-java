package com.example.javaprojet.dto;

import com.example.javaprojet.entity.ListeDiffusion;
import com.example.javaprojet.entity.Projet;
import lombok.*;

import java.util.Date;

@Data
@Getter
@Setter
@NoArgsConstructor
public class ListDiffusionDTO {
    private String type;
    private String chemin;
    private String nom;
    private Long id ;
    private String description;
    private boolean estSysteme;
    private Long projetId;
    private Date dateCreation;
    public ListDiffusionDTO(ListeDiffusion listeDiffusion) {
        setId(listeDiffusion.getId());
        setType(listeDiffusion.getType());
        setChemin(listeDiffusion.getChemin());
        setNom(listeDiffusion.getNom());
        setDescription(listeDiffusion.getDescription());
        setDateCreation(listeDiffusion.getDateCreation());
        setEstSysteme(listeDiffusion.isEstSysteme());
        setProjetId(listeDiffusion.getProjet().getId());
    }

    public ListDiffusionDTO(String type, String chemin, String nom, Long id, String description, boolean estSysteme, Long projet, Date dateCreation) {
        this.type = type;
        this.chemin = chemin;
        this.nom = nom;
        this.id = id;
        this.description = description;
        this.estSysteme = estSysteme;
        this.projetId = projet;
        this.dateCreation = dateCreation;
    }
}
