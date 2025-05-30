package com.example.javaprojet.dto;

import com.example.javaprojet.entity.Groupe;
import com.example.javaprojet.entity.Projet;
import com.example.javaprojet.entity.Utilisateur;
import jakarta.persistence.*;
import lombok.Data;
import net.minidev.json.annotate.JsonIgnore;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
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
