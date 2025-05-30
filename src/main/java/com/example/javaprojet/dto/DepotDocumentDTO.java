package com.example.javaprojet.dto;

import com.example.javaprojet.entity.DepotDocument;
import com.example.javaprojet.entity.Groupe;
import com.example.javaprojet.entity.Projet;

import lombok.Data;
import net.minidev.json.annotate.JsonIgnore;
@Data
public class DepotDocumentDTO {

    private boolean estPublic;
    private Projet projet;

    public DepotDocumentDTO(DepotDocument depotDocument) {
        setProjet(depotDocument.getProjet());
        setEstPublic(depotDocument.isEstPublic());
    }
}
