package com.example.javaprojet.dto;

import com.example.javaprojet.entity.DepotDocument;
import com.example.javaprojet.entity.Groupe;
import com.example.javaprojet.entity.Projet;

import jakarta.persistence.*;
import lombok.Data;
import net.minidev.json.annotate.JsonIgnore;

import java.util.Date;

@Data
public class DepotDocumentDTO {

    private Long id;

    private String nom;

    private String type;

    private String chemin;

    private Date dateCreation;
    private boolean estPublic;
    private Projet projet;

    public DepotDocumentDTO(DepotDocument depotDocument) {
        setProjet(depotDocument.getProjet());
        setEstPublic(depotDocument.isEstPublic());
        setNom(depotDocument.getNom());
        setType(depotDocument.getType());
        setChemin(depotDocument.getChemin());
        setDateCreation(depotDocument.getDateCreation());
        setId(depotDocument.getId());

    }
}
