package com.example.javaprojet.dto;

import com.example.javaprojet.entity.Groupe;
import lombok.Data;

import java.util.Date;

@Data
public class GroupeDTO {

    private Long id;
    private String nom;
    private String description;
    private boolean estSysteme;
    private Date dateCreation;

    public GroupeDTO(Groupe groupe) {
        setId(groupe.getId());
        setNom(groupe.getNom());
        setDescription(groupe.getDescription());
        setEstSysteme(groupe.isEstSysteme());
        setDateCreation(groupe.getDateCreation());
    }
}
